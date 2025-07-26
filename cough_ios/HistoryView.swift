//
//  HistoryView.swift
//  cough
//
//  Created by Assistant on 7/19/25.
//

import SwiftUI
import Firebase
import FirebaseFunctions
import FirebaseFirestore
import FirebaseAuth

struct HistoryView: View {
    @StateObject private var viewModel = HistoryViewModel()
    @State private var selectedAnalysis: CoughAnalysisResult?
    @Binding var isPresented: Bool
    
    var body: some View {
        ZStack {
            // Background gradient
            LinearGradient(
                colors: [
                    Color(red: 0.05, green: 0.1, blue: 0.2),
                    Color(red: 0.1, green: 0.15, blue: 0.25)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            if viewModel.isLoading {
                ProgressView("Loading history...")
                    .foregroundColor(.white)
            } else if viewModel.analyses.isEmpty {
                VStack(spacing: 20) {
                    Image(systemName: "clock.badge.questionmark")
                        .font(.system(size: 60))
                        .foregroundColor(.white.opacity(0.5))
                    
                    Text("No Analysis History")
                        .font(.title2)
                        .fontWeight(.semibold)
                        .foregroundColor(.white)
                    
                    Text("Your cough analyses will appear here")
                        .font(.subheadline)
                        .foregroundColor(.white.opacity(0.7))
                }
            } else {
                VStack(spacing: 0) {
                    Text("Analysis History")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal)
                        .padding(.top, 20)
                        .padding(.bottom, 10)
                    
                    List {
                        ForEach(viewModel.analyses) { analysis in
                            HistoryItemCard(analysis: analysis)
                                .listRowBackground(Color.clear)
                                .listRowSeparator(.hidden)
                                .listRowInsets(EdgeInsets(top: 5, leading: 16, bottom: 5, trailing: 16))
                                .onTapGesture {
                                    print("Tapped analysis: \(analysis.analysisId)")
                                    print("CoughType: \(analysis.coughType), Severity: \(analysis.severity)")
                                    selectedAnalysis = analysis
                                }
                                .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                                    Button(role: .destructive) {
                                        viewModel.deleteAnalysis(analysis)
                                    } label: {
                                        Label("Delete", systemImage: "trash")
                                    }
                                }
                        }
                        
                        HStack {
                            Spacer()
                            Text("\(viewModel.analyses.count) analyses recorded")
                                .font(.caption)
                                .foregroundColor(.white.opacity(0.5))
                                .padding(.vertical, 20)
                            Spacer()
                        }
                        .listRowBackground(Color.clear)
                        .listRowSeparator(.hidden)
                    }
                    .listStyle(.plain)
                    .scrollContentBackground(.hidden)
                }
            }
            
            // Close Button - same style as analysis view
            VStack {
                HStack {
                    Spacer()
                    Button(action: {
                        isPresented = false
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title)
                            .foregroundColor(.white.opacity(0.6))
                            .background(
                                Circle()
                                    .fill(.ultraThinMaterial)
                            )
                    }
                    .padding()
                }
                Spacer()
            }
        }
        .onAppear {
            viewModel.fetchHistory()
        }
        .sheet(item: $selectedAnalysis) { analysis in
            NavigationView {
                CoughAnalysisResultView(result: analysis, isPresented: Binding(
                    get: { selectedAnalysis != nil },
                    set: { if !$0 { selectedAnalysis = nil } }
                ))
            }
        }
    }
}

struct HistoryItemCard: View {
    let analysis: CoughAnalysisResult
    
    var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter
    }
    
    var body: some View {
        HStack(spacing: 15) {
            // Icon
            Circle()
                .fill(analysis.severityColor.opacity(0.2))
                .frame(width: 50, height: 50)
                .overlay(
                    Image(systemName: "waveform.path")
                        .font(.title3)
                        .foregroundColor(analysis.severityColor)
                )
            
            // Details
            VStack(alignment: .leading, spacing: 5) {
                Text(dateFormatter.string(from: Date(timeIntervalSince1970: analysis.timestamp)))
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.white)
                
                HStack(spacing: 10) {
                    Label(analysis.coughType.capitalized, systemImage: "drop.fill")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.8))
                    
                    Label(analysis.severity.capitalized, systemImage: "gauge")
                        .font(.caption)
                        .foregroundColor(analysis.severityColor)
                }
                
                Text("\(analysis.duration)")
                    .font(.caption2)
                    .foregroundColor(.white.opacity(0.6))
            }
            
            Spacer()
            
            // Confidence
            VStack {
                Text("\(Int(analysis.confidence * 100))%")
                    .font(.headline)
                    .foregroundColor(.white)
                Text("confidence")
                    .font(.caption2)
                    .foregroundColor(.white.opacity(0.5))
            }
            
            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.white.opacity(0.3))
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 15)
                .fill(.ultraThinMaterial)
                .overlay(
                    RoundedRectangle(cornerRadius: 15)
                        .stroke(Color.white.opacity(0.1), lineWidth: 1)
                )
        )
    }
}

class HistoryViewModel: ObservableObject {
    @Published var analyses: [CoughAnalysisResult] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    private let functions = Functions.functions(region: "us-central1")
    
    func deleteAnalysis(_ analysis: CoughAnalysisResult) {
        // Optimistically remove from UI
        withAnimation {
            analyses.removeAll { $0.id == analysis.id }
        }
        
        Task {
            do {
                guard let userId = Auth.auth().currentUser?.uid else {
                    throw NSError(domain: "HistoryView", code: 401, userInfo: [NSLocalizedDescriptionKey: "User not authenticated"])
                }
                
                // Delete directly from Firestore
                let db = Firestore.firestore()
                try await db.collection("users")
                    .document(userId)
                    .collection("analyses")
                    .document(analysis.analysisId)
                    .delete()
                
                print("Successfully deleted analysis: \(analysis.analysisId)")
                
                // Update total count
                try await db.collection("users").document(userId).updateData([
                    "totalAnalyses": FieldValue.increment(Int64(-1))
                ])
                
            } catch {
                // If deletion failed, add it back
                await MainActor.run {
                    self.analyses.append(analysis)
                    self.analyses.sort { $0.timestamp > $1.timestamp }
                    self.errorMessage = "Failed to delete: \(error.localizedDescription)"
                    print("Error deleting analysis: \(error)")
                }
            }
        }
    }
    
    func fetchHistory() {
        isLoading = true
        errorMessage = nil
        
        Task {
            do {
                let result = try await functions.httpsCallable("getAnalysisHistory").call(["limit": 50])
                
                if let data = result.data as? [String: Any],
                   let historyArray = data["history"] as? [[String: Any]] {
                    
                    let parsedAnalyses = historyArray.compactMap { item -> CoughAnalysisResult? in
                        guard let results = item["results"] as? [String: Any],
                              let insights = item["insights"] as? [String: Any] else {
                            print("Missing results or insights for item: \(item)")
                            return nil
                        }
                        
                        let coughType = results["coughType"] as? String ?? "unknown"
                        let severity = results["severity"] as? String ?? "mild"
                        
                        // Handle "none" severity/type from no-cough detections
                        let normalizedSeverity = (severity == "none") ? "mild" : severity
                        let urgency = results["urgency"] as? String ?? "routine"
                        let normalizedUrgency = (urgency == "none") ? "routine" : urgency
                        
                        print("Parsing analysis - Type: \(coughType), Severity: \(severity), Urgency: \(urgency)")
                        
                        return CoughAnalysisResult(
                            analysisId: item["id"] as? String ?? item["analysisId"] as? String ?? "",
                            timestamp: item["timestamp"] as? Double ?? 0,
                            coughType: coughType,
                            severity: normalizedSeverity,
                            characteristics: results["characteristics"] as? [String] ?? [],
                            potentialCauses: parsePotentialCauses(from: results["potentialCauses"] as? [[String: Any]] ?? []),
                            managementApproaches: results["managementApproaches"] as? [String] ?? [],
                            urgency: normalizedUrgency,
                            confidence: results["confidence"] as? Double ?? 0.5,
                            soundPattern: insights["soundPattern"] as? String ?? "",
                            frequency: insights["frequency"] as? String ?? "",
                            duration: insights["duration"] as? String ?? "",
                            additionalNotes: insights["additionalNotes"] as? [String] ?? []
                        )
                    }
                    
                    await MainActor.run {
                        self.analyses = parsedAnalyses
                        self.isLoading = false
                    }
                } else {
                    await MainActor.run {
                        self.isLoading = false
                        self.errorMessage = "Invalid data format"
                    }
                }
            } catch {
                await MainActor.run {
                    self.isLoading = false
                    self.errorMessage = error.localizedDescription
                    print("Error fetching history: \(error)")
                }
            }
        }
    }
    
    private func parsePotentialCauses(from data: [[String: Any]]) -> [PotentialCause] {
        return data.compactMap { cause in
            guard let condition = cause["condition"] as? String,
                  let likelihood = cause["likelihood"] as? String,
                  let description = cause["description"] as? String else {
                return nil
            }
            return PotentialCause(condition: condition, likelihood: likelihood, description: description)
        }
    }
}

#Preview {
    HistoryView(isPresented: .constant(true))
}