//
//  CoughAnalysisResultView.swift
//  cough
//
//  Created by Reid Glaze on 7/17/25.
//

import SwiftUI
import Firebase
import FirebaseFirestore
import FirebaseAuth

struct CoughAnalysisResultView: View {
    let result: CoughAnalysisResult
    @Binding var isPresented: Bool
    @State private var showReportSheet = false
    @State private var reportText = ""
    @State private var showReportConfirmation = false
    
    var body: some View {
        ZStack {
            // Dark gradient background
            LinearGradient(
                colors: [
                    Color(red: 0.05, green: 0.1, blue: 0.2),
                    Color(red: 0.1, green: 0.15, blue: 0.25)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            ScrollView {
                VStack(spacing: 25) {
                    // Header
                    VStack(spacing: 15) {
                        Image(systemName: result.coughType == "none" ? "exclamationmark.circle.fill" : "checkmark.seal.fill")
                            .font(.system(size: 60))
                            .foregroundColor(result.coughType == "none" ? .orange : .green)
                        
                        Text(result.coughType == "none" ? "No Cough Detected" : "Analysis Complete")
                            .font(.system(size: 32, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                        
                        Text(Date(timeIntervalSince1970: result.timestamp), style: .date)
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.6))
                    }
                    .padding(.top, 40)
                    
                    // Main Results Card
                    VStack(spacing: 20) {
                        // Only show type and severity if cough was detected
                        if result.coughType != "none" {
                            // Cough Type and Severity
                            HStack(spacing: 20) {
                                ResultCard(
                                    title: "Type",
                                    value: result.coughType.capitalized,
                                    icon: "waveform.path",
                                    color: .blue
                                )
                                
                                ResultCard(
                                    title: "Severity",
                                    value: result.severity.capitalized,
                                    icon: "gauge.with.needle.fill",
                                    color: result.severityColor
                                )
                            }
                        }
                        
                        // Urgency - only show if cough detected
                        if result.coughType != "none" {
                            GlassmorphicCard()
                                .overlay(
                                    HStack {
                                        Image(systemName: "exclamationmark.triangle.fill")
                                            .font(.title2)
                                            .foregroundColor(result.urgencyColor)
                                        
                                        VStack(alignment: .leading, spacing: 5) {
                                            Text("Medical Attention")
                                                .font(.caption)
                                                .foregroundColor(.white.opacity(0.7))
                                            
                                            Text(result.urgency.capitalized)
                                                .font(.headline)
                                                .foregroundColor(.white)
                                        }
                                        
                                        Spacer()
                                        
                                        Text("\(Int(result.confidence * 100))% Confidence")
                                            .font(.caption)
                                            .foregroundColor(.white.opacity(0.6))
                                    }
                                    .padding()
                                )
                                .frame(height: 80)
                        }
                        
                        // Characteristics
                        if !result.characteristics.isEmpty && result.coughType != "none" {
                            VStack(alignment: .leading, spacing: 10) {
                                Text("Characteristics")
                                    .font(.headline)
                                    .foregroundColor(.white)
                                
                                FlowLayout(spacing: 10) {
                                    ForEach(result.characteristics, id: \.self) { characteristic in
                                        if characteristic != "No cough detected" {
                                            CharacteristicChip(text: characteristic)
                                        }
                                    }
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }
                        
                        // No cough message
                        if result.coughType == "none" {
                            VStack(spacing: 15) {
                                Text("No cough was detected in this recording")
                                    .font(.subheadline)
                                    .foregroundColor(.white.opacity(0.9))
                                    .multilineTextAlignment(.center)
                                
                                Text("Please record a clear cough sound for analysis")
                                    .font(.caption)
                                    .foregroundColor(.white.opacity(0.7))
                                    .multilineTextAlignment(.center)
                            }
                            .padding(.vertical, 20)
                            .frame(maxWidth: .infinity)
                        }
                        
                        
                        // Detailed Analysis - Always visible
                        VStack(spacing: 25) {
                            // Potential Causes
                            if !result.potentialCauses.isEmpty {
                                VStack(alignment: .leading, spacing: 15) {
                                    Text("Potential Causes")
                                        .font(.headline)
                                        .foregroundColor(.white)
                                        .padding(.top, 10)
                                    
                                    ForEach(result.potentialCauses) { cause in
                                        // Simple text layout without card
                                        VStack(alignment: .leading, spacing: 5) {
                                            HStack(spacing: 8) {
                                                Circle()
                                                    .fill(cause.likelihoodColor)
                                                    .frame(width: 8, height: 8)
                                                
                                                Text(cause.condition)
                                                    .font(.subheadline)
                                                    .fontWeight(.semibold)
                                                    .foregroundColor(.white)
                                                
                                                Text("(\(cause.likelihood))")
                                                    .font(.caption)
                                                    .foregroundColor(cause.likelihoodColor)
                                            }
                                            
                                            Text(cause.description)
                                                .font(.caption)
                                                .foregroundColor(.white.opacity(0.8))
                                                .padding(.leading, 16)
                                        }
                                    }
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                            }
                            
                            // Commonly Discussed Management Approaches
                            if !result.managementApproaches.isEmpty {
                                VStack(alignment: .leading, spacing: 15) {
                                    Text("Commonly Discussed Management Approaches")
                                        .font(.headline)
                                        .foregroundColor(.white)
                                        .padding(.top, 10)
                                    
                                    ForEach(result.managementApproaches, id: \.self) { approach in
                                        HStack(alignment: .top, spacing: 10) {
                                            Image(systemName: "info.circle.fill")
                                                .font(.caption)
                                                .foregroundColor(.white.opacity(0.8))
                                                .padding(.top, 2)
                                            
                                            Text(approach)
                                                .font(.subheadline)
                                                .foregroundColor(.white.opacity(0.9))
                                                .fixedSize(horizontal: false, vertical: true)
                                            
                                            Spacer()
                                        }
                                    }
                                    
                                    Text("These are general approaches discussed in medical literature. Always consult a healthcare provider for personalized advice.")
                                        .font(.caption2)
                                        .foregroundColor(.white.opacity(0.6))
                                        .italic()
                                        .padding(.top, 5)
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                            }
                            
                            // Sound Analysis
                            VStack(alignment: .leading, spacing: 15) {
                                Text("Sound Analysis")
                                    .font(.headline)
                                    .foregroundColor(.white)
                                    .padding(.top, 10)
                                
                                VStack(alignment: .leading, spacing: 10) {
                                    SoundAnalysisRow(label: "Pattern", value: result.soundPattern)
                                    SoundAnalysisRow(label: "Frequency", value: result.frequency)
                                    SoundAnalysisRow(label: "Duration", value: result.duration)
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            
                            // Additional Notes
                            if !result.additionalNotes.isEmpty {
                                VStack(alignment: .leading, spacing: 15) {
                                    Text("Additional Notes")
                                        .font(.headline)
                                        .foregroundColor(.white)
                                        .padding(.top, 10)
                                    
                                    ForEach(result.additionalNotes, id: \.self) { note in
                                        Text("• \(note)")
                                            .font(.subheadline)
                                            .foregroundColor(.white.opacity(0.8))
                                            .fixedSize(horizontal: false, vertical: true)
                                    }
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                            }
                        }
                    }
                    .padding(25)
                    .background(
                        GlassmorphicCard()
                    )
                    .padding(.horizontal)
                    
                    // Disclaimer
                    Text("This analysis is for informational purposes only and should not replace professional medical advice. Please consult a healthcare provider for proper diagnosis and treatment.")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.5))
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                        .padding(.bottom, 10)
                    
                    // Medical Sources Section
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Medical References")
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding(.top, 10)
                        
                        VStack(alignment: .leading, spacing: 10) {
                            Link(destination: URL(string: "https://medlineplus.gov/")!) {
                                HStack {
                                    Image(systemName: "link.circle.fill")
                                        .foregroundColor(.white.opacity(0.8))
                                        .font(.caption)
                                    Text("MedlinePlus - NIH Health Information")
                                        .font(.caption)
                                        .foregroundColor(.white.opacity(0.9))
                                    Spacer()
                                    Image(systemName: "arrow.up.right.square")
                                        .font(.caption2)
                                        .foregroundColor(.white.opacity(0.6))
                                }
                            }
                            
                            Link(destination: URL(string: "https://www.cdc.gov/")!) {
                                HStack {
                                    Image(systemName: "link.circle.fill")
                                        .foregroundColor(.white.opacity(0.8))
                                        .font(.caption)
                                    Text("CDC - Centers for Disease Control")
                                        .font(.caption)
                                        .foregroundColor(.white.opacity(0.9))
                                    Spacer()
                                    Image(systemName: "arrow.up.right.square")
                                        .font(.caption2)
                                        .foregroundColor(.white.opacity(0.6))
                                }
                            }
                            
                            Link(destination: URL(string: "https://www.ncbi.nlm.nih.gov/")!) {
                                HStack {
                                    Image(systemName: "link.circle.fill")
                                        .foregroundColor(.white.opacity(0.8))
                                        .font(.caption)
                                    Text("NCBI - National Center for Biotechnology")
                                        .font(.caption)
                                        .foregroundColor(.white.opacity(0.9))
                                    Spacer()
                                    Image(systemName: "arrow.up.right.square")
                                        .font(.caption2)
                                        .foregroundColor(.white.opacity(0.6))
                                }
                            }
                            
                            Link(destination: URL(string: "https://www.nhs.uk/")!) {
                                HStack {
                                    Image(systemName: "link.circle.fill")
                                        .foregroundColor(.white.opacity(0.8))
                                        .font(.caption)
                                    Text("NHS - UK National Health Service")
                                        .font(.caption)
                                        .foregroundColor(.white.opacity(0.9))
                                    Spacer()
                                    Image(systemName: "arrow.up.right.square")
                                        .font(.caption2)
                                        .foregroundColor(.white.opacity(0.6))
                                }
                            }
                            
                            Link(destination: URL(string: "https://www.who.int/")!) {
                                HStack {
                                    Image(systemName: "link.circle.fill")
                                        .foregroundColor(.white.opacity(0.8))
                                        .font(.caption)
                                    Text("WHO - World Health Organization")
                                        .font(.caption)
                                        .foregroundColor(.white.opacity(0.9))
                                    Spacer()
                                    Image(systemName: "arrow.up.right.square")
                                        .font(.caption2)
                                        .foregroundColor(.white.opacity(0.6))
                                }
                            }
                        }
                        .padding(.horizontal)
                        .padding(.vertical, 15)
                        .background(
                            RoundedRectangle(cornerRadius: 15)
                                .fill(.ultraThinMaterial)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 15)
                                        .stroke(Color.white.opacity(0.1), lineWidth: 1)
                                )
                        )
                        
                        Text("AI analysis is based on patterns from medical literature. Sources are provided for educational reference only.")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.4))
                            .italic()
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 20)
                    
                    // Report Button
                    Button(action: {
                        showReportSheet = true
                    }) {
                        HStack {
                            Image(systemName: "exclamationmark.triangle")
                                .font(.caption)
                            Text("Report harmful content")
                                .font(.caption)
                                .fontWeight(.medium)
                        }
                        .foregroundColor(.red.opacity(0.8))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(
                            RoundedRectangle(cornerRadius: 20)
                                .fill(.ultraThinMaterial)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 20)
                                        .stroke(Color.red.opacity(0.3), lineWidth: 1)
                                )
                        )
                    }
                    .padding(.bottom, 30)
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
        .sheet(isPresented: $showReportSheet) {
            ReportContentSheet(
                isPresented: $showReportSheet,
                reportText: $reportText,
                showConfirmation: $showReportConfirmation,
                analysisId: result.analysisId,
                analysisData: [
                    "coughType": result.coughType,
                    "severity": result.severity,
                    "timestamp": result.timestamp,
                    "confidence": result.confidence
                ]
            )
        }
        .alert("Report Submitted", isPresented: $showReportConfirmation) {
            Button("OK", role: .cancel) { }
        } message: {
            Text("Thank you for your report. We will review it shortly.")
        }
    }
}

struct ResultCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    
    var body: some View {
        GlassmorphicCard()
            .overlay(
                VStack(spacing: 10) {
                    Image(systemName: icon)
                        .font(.title2)
                        .foregroundColor(color)
                    
                    Text(title)
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.7))
                    
                    Text(value)
                        .font(.headline)
                        .foregroundColor(.white)
                }
                .padding()
            )
            .frame(height: 100)
    }
}

struct CharacteristicChip: View {
    let text: String
    
    var body: some View {
        Text(text)
            .font(.caption)
            .foregroundColor(.white)
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(
                Capsule()
                    .fill(.ultraThinMaterial)
                    .overlay(
                        Capsule()
                            .stroke(Color.white.opacity(0.2), lineWidth: 1)
                    )
            )
    }
}

struct PotentialCauseCard: View {
    let cause: PotentialCause
    
    var body: some View {
        GlassmorphicCard()
            .overlay(
                HStack(alignment: .top, spacing: 15) {
                    Circle()
                        .fill(cause.likelihoodColor.opacity(0.2))
                        .frame(width: 40, height: 40)
                        .overlay(
                            Text(String(cause.likelihood.prefix(1)).uppercased())
                                .font(.headline)
                                .foregroundColor(cause.likelihoodColor)
                        )
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text(cause.condition)
                            .font(.subheadline)
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .lineLimit(2)
                            .fixedSize(horizontal: false, vertical: true)
                        
                        Text(cause.description)
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.7))
                            .lineLimit(3)
                            .fixedSize(horizontal: false, vertical: true)
                        
                        Text("Likelihood: \(cause.likelihood.capitalized)")
                            .font(.caption2)
                            .foregroundColor(cause.likelihoodColor)
                            .padding(.top, 2)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
                    Spacer(minLength: 0)
                }
                .padding()
            )
            .frame(minHeight: 100)
    }
}

struct SoundAnalysisRow: View {
    let label: String
    let value: String
    
    var body: some View {
        HStack {
            Text(label)
                .font(.subheadline)
                .foregroundColor(.white.opacity(0.6))
            
            Spacer()
            
            Text(value)
                .font(.subheadline)
                .foregroundColor(.white.opacity(0.9))
        }
        .padding(.vertical, 5)
    }
}

struct FlowLayout: Layout {
    var spacing: CGFloat = 10
    
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let result = FlowResult(
            in: proposal.replacingUnspecifiedDimensions().width,
            subviews: subviews,
            spacing: spacing
        )
        return result.size
    }
    
    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let result = FlowResult(
            in: bounds.width,
            subviews: subviews,
            spacing: spacing
        )
        for (index, frame) in result.frames.enumerated() {
            subviews[index].place(at: CGPoint(x: frame.origin.x + bounds.origin.x,
                                             y: frame.origin.y + bounds.origin.y),
                                 proposal: ProposedViewSize(frame.size))
        }
    }
    
    struct FlowResult {
        var frames: [CGRect] = []
        var size: CGSize = .zero
        
        init(in maxWidth: CGFloat, subviews: Subviews, spacing: CGFloat) {
            var currentX: CGFloat = 0
            var currentY: CGFloat = 0
            var lineHeight: CGFloat = 0
            var maxX: CGFloat = 0
            
            for subview in subviews {
                let dimensions = subview.dimensions(in: ProposedViewSize(width: maxWidth, height: .infinity))
                
                if currentX + dimensions.width > maxWidth && currentX > 0 {
                    currentY += lineHeight + spacing
                    currentX = 0
                    lineHeight = 0
                }
                
                frames.append(CGRect(origin: CGPoint(x: currentX, y: currentY),
                                   size: CGSize(width: dimensions.width, height: dimensions.height)))
                
                currentX += dimensions.width + spacing
                maxX = max(maxX, currentX)
                lineHeight = max(lineHeight, dimensions.height)
            }
            
            size = CGSize(width: maxX - spacing, height: currentY + lineHeight)
        }
    }
}

struct ReportContentSheet: View {
    @Binding var isPresented: Bool
    @Binding var reportText: String
    @Binding var showConfirmation: Bool
    let analysisId: String
    let analysisData: [String: Any]
    
    @State private var isSubmitting = false
    @FocusState private var isTextFieldFocused: Bool
    
    var body: some View {
        NavigationView {
            ZStack {
                // Dark background for entire view
                Color(red: 0.05, green: 0.1, blue: 0.2)
                    .ignoresSafeArea()
                
                VStack(spacing: 0) {
                    ScrollView {
                        VStack(spacing: 20) {
                    // Warning icon
                    Image(systemName: "exclamationmark.triangle.fill")
                        .font(.system(size: 50))
                        .foregroundColor(.red.opacity(0.8))
                        .padding(.top, 20)
                    
                    Text("Report Harmful Content")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    
                    Text("Please describe why you believe this content may be harmful or inappropriate.")
                        .font(.subheadline)
                        .foregroundColor(.white.opacity(0.7))
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                    
                    // Text input
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Your Report")
                            .font(.caption)
                            .foregroundColor(.white.opacity(0.6))
                        
                        TextEditor(text: $reportText)
                            .scrollContentBackground(.hidden)
                            .foregroundColor(.white)
                            .padding(12)
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(.ultraThinMaterial)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(Color.white.opacity(0.2), lineWidth: 1)
                                    )
                            )
                            .frame(minHeight: 150)
                            .focused($isTextFieldFocused)
                        
                        Text("\(reportText.count)/500")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.4))
                            .frame(maxWidth: .infinity, alignment: .trailing)
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 40) // Increased padding
                }
                }
                
                // Spacer to push button away from content
                Spacer(minLength: 20)
                
                // Submit button - outside scroll view
                Button(action: submitReport) {
                    if isSubmitting {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Text("Submit Report")
                            .fontWeight(.semibold)
                    }
                }
                .disabled(reportText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || isSubmitting || reportText.count > 500)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                .background(
                    RoundedRectangle(cornerRadius: 25)
                        .fill(reportText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || reportText.count > 500 ? Color.red.opacity(0.3) : Color.red.opacity(0.8))
                )
                .padding(.horizontal)
                .padding(.bottom, 20)
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        isPresented = false
                    }
                    .foregroundColor(.white.opacity(0.8))
                }
            }
            .onAppear {
                isTextFieldFocused = true
            }
        }
    }
    
    func submitReport() {
        guard let userId = Auth.auth().currentUser?.uid else { 
            print("No authenticated user found")
            return 
        }
        
        print("Submitting report for analysis: \(analysisId)")
        
        isSubmitting = true
        let db = Firestore.firestore()
        
        var reportData: [String: Any] = [
            "reportedBy": userId,
            "reportedAt": FieldValue.serverTimestamp(),
            "reason": reportText.trimmingCharacters(in: .whitespacesAndNewlines),
            "analysisId": analysisId,
            "analysisData": analysisData,
            "status": "pending"
        ]
        
        // Add user email if available
        if let email = Auth.auth().currentUser?.email {
            reportData["reporterEmail"] = email
        }
        
        print("Report data: \(reportData)")
        
        db.collection("reports").addDocument(data: reportData) { error in
            DispatchQueue.main.async {
                self.isSubmitting = false
                
                if let error = error {
                    print("Error submitting report: \(error)")
                    print("Error code: \((error as NSError).code)")
                    print("Error domain: \((error as NSError).domain)")
                } else {
                    print("Report submitted successfully")
                    self.reportText = ""
                    self.isPresented = false
                    self.showConfirmation = true
                }
            }
        }
    }
}