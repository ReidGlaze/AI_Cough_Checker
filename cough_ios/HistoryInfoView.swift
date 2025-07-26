//
//  HistoryInfoView.swift
//  cough
//
//  Created by Assistant on 7/19/25.
//

import SwiftUI
import Firebase
import FirebaseFunctions
import FirebaseFirestore
import FirebaseAuth

struct HistoryInfoView: View {
    @State private var selectedTab = 0
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
            
            VStack(spacing: 0) {
                // Header with tabs
                VStack(spacing: 0) {
                    // Title
                    HStack {
                        Text(selectedTab == 0 ? "History" : "Information")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                            .foregroundColor(.white)
                        
                        Spacer()
                    }
                    .padding(.horizontal)
                    .padding(.top, 20)
                    .padding(.bottom, 20)
                    
                    // Tab selector
                    HStack(spacing: 0) {
                        TabButton(title: "History", isSelected: selectedTab == 0) {
                            withAnimation { selectedTab = 0 }
                        }
                        
                        TabButton(title: "Info", isSelected: selectedTab == 1) {
                            withAnimation { selectedTab = 1 }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 10)
                }
                
                // Content
                if selectedTab == 0 {
                    HistoryTabView()
                } else {
                    InfoTabView()
                }
            }
            
            // Close Button
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
    }
}

struct TabButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Text(title)
                    .font(.headline)
                    .foregroundColor(isSelected ? .white : .white.opacity(0.6))
                
                Rectangle()
                    .fill(isSelected ? Color.white : Color.clear)
                    .frame(height: 2)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

struct HistoryTabView: View {
    @StateObject private var viewModel = HistoryViewModel()
    @State private var selectedAnalysis: CoughAnalysisResult?
    
    var body: some View {
        Group {
            if viewModel.isLoading {
                ProgressView("Loading history...")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
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
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                List {
                    ForEach(viewModel.analyses) { analysis in
                        HistoryItemCard(analysis: analysis)
                            .listRowBackground(Color.clear)
                            .listRowSeparator(.hidden)
                            .listRowInsets(EdgeInsets(top: 5, leading: 16, bottom: 5, trailing: 16))
                            .onTapGesture {
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
        .onAppear {
            viewModel.fetchHistory()
            // Increment history view count for review prompt
            AppReviewManager.shared.incrementHistoryViewCount()
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

struct InfoTabView: View {
    @State private var showingDeleteAlert = false
    @State private var showingSignOutAlert = false
    @State private var showingPrivacyPolicy = false
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 30) {
                // Important Notes Section
                VStack(alignment: .leading, spacing: 15) {
                    Text("Important Notes")
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    VStack(alignment: .leading, spacing: 12) {
                        InfoRow(icon: "person.fill", text: "For human coughs only")
                        InfoRow(icon: "graduationcap.fill", text: "Educational purposes - not medical advice")
                        InfoRow(icon: "waveform.circle", text: "Record in quiet environment for best results")
                        InfoRow(icon: "speaker.slash.fill", text: "Background noise affects accuracy")
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
                
                // Account Section
                VStack(alignment: .leading, spacing: 15) {
                    Text("Account")
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    VStack(spacing: 0) {
                        // User email
                        if let email = Auth.auth().currentUser?.email {
                            HStack {
                                Image(systemName: "envelope.fill")
                                    .foregroundColor(.white.opacity(0.7))
                                Text(email)
                                    .foregroundColor(.white.opacity(0.9))
                                Spacer()
                            }
                            .padding()
                            
                            Divider()
                                .background(Color.white.opacity(0.1))
                        }
                        
                        
                        // Delete Account
                        Button(action: {
                            showingDeleteAlert = true
                        }) {
                            HStack {
                                Image(systemName: "trash.circle.fill")
                                    .foregroundColor(.white.opacity(0.7))
                                Text("Delete Account")
                                    .foregroundColor(.white)
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .foregroundColor(.white.opacity(0.3))
                            }
                            .padding()
                        }
                    }
                    .background(
                        RoundedRectangle(cornerRadius: 15)
                            .fill(.ultraThinMaterial)
                            .overlay(
                                RoundedRectangle(cornerRadius: 15)
                                    .stroke(Color.white.opacity(0.1), lineWidth: 1)
                            )
                    )
                }
                
                // Support Section
                VStack(alignment: .leading, spacing: 15) {
                    Text("Support")
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    Button(action: {
                        AppReviewManager.shared.requestReviewManually()
                    }) {
                        HStack {
                            Image(systemName: "star.fill")
                                .foregroundColor(.white.opacity(0.7))
                            Text("Rate Cough Checker")
                                .foregroundColor(.white)
                            Spacer()
                            Image(systemName: "chevron.right")
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
                
                // Legal Section
                VStack(alignment: .leading, spacing: 15) {
                    Text("Legal")
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    Button(action: {
                        showingPrivacyPolicy = true
                    }) {
                        HStack {
                            Image(systemName: "lock.shield.fill")
                                .foregroundColor(.white.opacity(0.7))
                            Text("Privacy Policy")
                                .foregroundColor(.white)
                            Spacer()
                            Image(systemName: "arrow.up.right.square")
                                .font(.caption)
                                .foregroundColor(.white.opacity(0.5))
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
                
                // App version
                HStack {
                    Spacer()
                    Text("Version \(Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0")")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.4))
                    Spacer()
                }
                .padding(.top, 20)
            }
            .padding()
        }
        .alert("Sign Out", isPresented: $showingSignOutAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Sign Out", role: .destructive) {
                signOut()
            }
        } message: {
            Text("Are you sure you want to sign out?")
        }
        .alert("Delete Account", isPresented: $showingDeleteAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                deleteAccount()
            }
        } message: {
            Text("This will permanently delete your account and all analysis history. This action cannot be undone.")
        }
        .sheet(isPresented: $showingPrivacyPolicy) {
            SafariView(url: URL(string: "https://aicoughchecker.vercel.app/privacy")!)
        }
    }
    
    private func signOut() {
        do {
            try Auth.auth().signOut()
            // Reset onboarding to show login screen
            UserDefaults.standard.set(false, forKey: "hasCompletedOnboarding")
        } catch {
            print("Error signing out: \(error)")
        }
    }
    
    private func deleteAccount() {
        Task {
            guard let user = Auth.auth().currentUser else { return }
            
            // Delete user data from Firestore
            let db = Firestore.firestore()
            do {
                // Delete all analyses
                let analyses = try await db.collection("users")
                    .document(user.uid)
                    .collection("analyses")
                    .getDocuments()
                
                for doc in analyses.documents {
                    try await doc.reference.delete()
                }
                
                // Delete user document
                try await db.collection("users").document(user.uid).delete()
                
                // Delete auth account
                try await user.delete()
                
                // Reset onboarding to show login screen - MUST be on main thread
                await MainActor.run {
                    UserDefaults.standard.set(false, forKey: "hasCompletedOnboarding")
                    UserDefaults.standard.synchronize() // Force sync
                    
                    // Also update the @AppStorage value if possible
                    if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                       let window = windowScene.windows.first {
                        // Force the app to re-evaluate by dismissing all sheets
                        window.rootViewController?.dismiss(animated: false)
                    }
                }
                
                print("Account deleted. hasCompletedOnboarding set to: \(UserDefaults.standard.bool(forKey: "hasCompletedOnboarding"))")
                
            } catch {
                print("Error deleting account: \(error)")
            }
        }
    }
}

struct InfoRow: View {
    let icon: String
    let text: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(.white.opacity(0.7))
                .frame(width: 25)
            Text(text)
                .font(.subheadline)
                .foregroundColor(.white.opacity(0.9))
            Spacer()
        }
    }
}

// Safari View for Privacy Policy
struct SafariView: UIViewControllerRepresentable {
    let url: URL
    
    func makeUIViewController(context: Context) -> SFSafariViewController {
        return SFSafariViewController(url: url)
    }
    
    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}
}

import SafariServices

#Preview {
    HistoryInfoView(isPresented: .constant(true))
}