//
//  ModernContentView.swift
//  cough
//
//  Created by Reid Glaze on 7/17/25.
//

import SwiftUI
import Firebase
import FirebaseAuth

struct ModernContentView: View {
    @StateObject private var authManager = AuthenticationManager()
    @StateObject private var audioRecorder = AudioRecorder()
    @State private var recordingScale: CGFloat = 1.0
    @State private var pulseAnimation = false
    @State private var showParticles = false
    @State private var waveformAnimation = false
    @State private var showResults = false
    @State private var showHistory = false

    private let impactLight = UIImpactFeedbackGenerator(style: .light)
    private let impactMedium = UIImpactFeedbackGenerator(style: .medium)
    private let notificationFeedback = UINotificationFeedbackGenerator()
    
    var body: some View {
        ZStack {
            // Animated gradient background (different from onboarding)
            ModernGradientBackground()
                .allowsHitTesting(false)
            
            VStack(spacing: 40) {
                // Top section with animated logo
                VStack(spacing: 20) {
                    ZStack {
                        // Pulsing glow effect
                        Circle()
                            .fill(
                                RadialGradient(
                                    colors: [.cyan.opacity(0.6), .blue.opacity(0.3), .clear],
                                    center: .center,
                                    startRadius: 0,
                                    endRadius: 80
                                )
                            )
                            .frame(width: 160, height: 160)
                            .blur(radius: 20)
                            .scaleEffect(pulseAnimation ? 1.3 : 0.9)
                            .animation(.easeInOut(duration: 3).repeatForever(autoreverses: true), value: pulseAnimation)
                        
                        Image(systemName: "waveform.path.ecg.rectangle")
                            .font(.system(size: 70))
                            .foregroundStyle(
                                LinearGradient(
                                    colors: [.cyan, .blue],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                )
                            )
                            .rotationEffect(.degrees(waveformAnimation ? 5 : -5))
                            .animation(.easeInOut(duration: 2).repeatForever(autoreverses: true), value: waveformAnimation)
                    }
                    
                    Text("Cough Checker")
                        .font(.system(size: 42, weight: .bold, design: .rounded))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [.white, .white.opacity(0.8)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )
                }
                .padding(.top, 60)
                
                Spacer()
                
                // Main recording button with glassmorphism
                Button(action: {
                    print("Recording button tapped")
                    toggleRecording()
                }) {
                    ZStack {
                        if showParticles && audioRecorder.isRecording {
                            RecordingParticlesView()
                                .allowsHitTesting(false)
                        }
                        
                        // Glassmorphic card behind button
                        RoundedRectangle(cornerRadius: 100)
                            .fill(.ultraThinMaterial)
                            .overlay(
                                RoundedRectangle(cornerRadius: 100)
                                    .stroke(
                                        LinearGradient(
                                            colors: [.white.opacity(0.6), .white.opacity(0.2)],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        ),
                                        lineWidth: 2
                                    )
                            )
                            .frame(width: 220, height: 220)
                            .shadow(color: .black.opacity(0.3), radius: 20, x: 0, y: 10)
                            .scaleEffect(recordingScale)
                        
                        // Animated circles when recording
                        if audioRecorder.isRecording {
                            ForEach(0..<3) { index in
                                Circle()
                                    .stroke(Color.red.opacity(0.3), lineWidth: 2)
                                    .scaleEffect(audioRecorder.isRecording ? 1 + CGFloat(index) * 0.3 : 1)
                                    .opacity(audioRecorder.isRecording ? 0 : 1)
                                    .animation(
                                        Animation.easeOut(duration: 1.5)
                                            .repeatForever(autoreverses: false)
                                            .delay(Double(index) * 0.3),
                                        value: audioRecorder.isRecording
                                    )
                            }
                        }
                        
                        // Main button content
                        VStack(spacing: 15) {
                                if audioRecorder.isAnalyzing {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                        .scaleEffect(2)
                                    
                                    Text("Analyzing...")
                                        .font(.headline)
                                        .foregroundColor(.white)
                                } else {
                                    Image(systemName: audioRecorder.isRecording ? "stop.circle.fill" : "mic.circle.fill")
                                        .font(.system(size: 80))
                                        .foregroundStyle(
                                            LinearGradient(
                                                colors: audioRecorder.isRecording ? [.red, .orange] : [.cyan, .blue],
                                                startPoint: .topLeading,
                                                endPoint: .bottomTrailing
                                            )
                                        )
                                        .scaleEffect(audioRecorder.isRecording ? 1.1 : 1.0)
                                        .animation(.easeInOut(duration: 0.3), value: audioRecorder.isRecording)
                                    
                                    if audioRecorder.isRecording {
                                        Text(String(format: "Recording... %.1fs", audioRecorder.recordingTime))
                                            .font(.headline)
                                            .foregroundColor(.white)
                                    } else {
                                        Text("Tap to Analyze")
                                            .font(.headline)
                                            .foregroundColor(.white)
                                    }
                                }
                            }
                        .frame(width: 200, height: 200)
                    }
                }
                .buttonStyle(PlainButtonStyle())
                .zIndex(10)
                
                Spacer()
                
                // Bottom navigation with glassmorphic style
                HStack(spacing: 60) {
                    Spacer()
                    Button(action: {
                        print("History button tapped")
                        impactLight.impactOccurred()
                        showHistory = true
                    }) {
                        NavigationButton(icon: "clock.arrow.circlepath", label: "History & Info", isActive: true)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .zIndex(10)
                    Spacer()
                }
                .padding(.horizontal, 30)
                .padding(.bottom, 50)
            }
        }
        .onAppear {
            pulseAnimation = true
            waveformAnimation = true
        }
        .sheet(isPresented: $showResults, onDismiss: {
            // Reset the analysis result when sheet is dismissed
            audioRecorder.analysisResult = nil
        }) {
            if let result = audioRecorder.analysisResult {
                CoughAnalysisResultView(result: result, isPresented: $showResults)
            }
        }
        .onChange(of: audioRecorder.analysisResult) { newValue in
            if newValue != nil {
                notificationFeedback.notificationOccurred(.success)
                showResults = true
                // Increment analysis count for review prompt
                AppReviewManager.shared.incrementAnalysisCount()
            }
        }
        .sheet(isPresented: $showHistory) {
            HistoryInfoView(isPresented: $showHistory)
        }
    }
    
    func toggleRecording() {
        if audioRecorder.isAnalyzing {
            return // Don't allow action while analyzing
        }

        withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) {
            if audioRecorder.isRecording {
                impactMedium.impactOccurred()
                audioRecorder.stopRecording()
                recordingScale = 1.0
                showParticles = false
            } else {
                impactMedium.impactOccurred()
                audioRecorder.startRecording()
                recordingScale = 1.1
                showParticles = true
            }
        }
        
        // Check for results after analysis
        if audioRecorder.analysisResult != nil {
            showResults = true
        }
    }
}

struct ModernGradientBackground: View {
    @State private var animateGradient = false
    
    var body: some View {
        LinearGradient(
            colors: [
                Color(red: 0.05, green: 0.1, blue: 0.2),
                Color(red: 0.1, green: 0.15, blue: 0.25),
                Color(red: 0.05, green: 0.2, blue: 0.3),
                Color(red: 0.02, green: 0.1, blue: 0.15)
            ],
            startPoint: animateGradient ? .topLeading : .bottomTrailing,
            endPoint: animateGradient ? .bottomTrailing : .topLeading
        )
        .ignoresSafeArea()
        .onAppear {
            withAnimation(.easeInOut(duration: 10).repeatForever(autoreverses: true)) {
                animateGradient.toggle()
            }
        }
        .overlay(
            // Subtle noise texture
            Rectangle()
                .fill(
                    LinearGradient(
                        colors: [.white.opacity(0.05), .clear],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .blendMode(.overlay)
        )
    }
}

struct NavigationButton: View {
    let icon: String
    let label: String
    let isActive: Bool
    
    var body: some View {
        VStack(spacing: 8) {
            ZStack {
                RoundedRectangle(cornerRadius: 20)
                    .fill(.ultraThinMaterial)
                    .frame(width: 60, height: 60)
                    .overlay(
                        RoundedRectangle(cornerRadius: 20)
                            .stroke(
                                LinearGradient(
                                    colors: isActive ? [.cyan.opacity(0.8), .blue.opacity(0.8)] : [.white.opacity(0.3), .white.opacity(0.1)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                ),
                                lineWidth: 1
                            )
                    )
                
                Image(systemName: icon)
                    .font(.system(size: 24))
                    .foregroundStyle(
                        LinearGradient(
                            colors: isActive ? [.cyan, .blue] : [.white.opacity(0.8), .white.opacity(0.6)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
            }
            
            Text(label)
                .font(.caption)
                .foregroundColor(.white.opacity(0.7))
        }
    }
}

struct RecordingParticlesView: View {
    @State private var particles: [RecordingParticle] = []
    
    struct RecordingParticle: Identifiable {
        let id = UUID()
        var position: CGPoint
        var opacity: Double
        var scale: CGFloat
        var color: Color
    }
    
    var body: some View {
        GeometryReader { geometry in
            ZStack {
                ForEach(particles) { particle in
                    Circle()
                        .fill(particle.color)
                        .frame(width: 6, height: 6)
                        .opacity(particle.opacity)
                        .scaleEffect(particle.scale)
                        .position(particle.position)
                }
            }
            .onAppear {
                Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { _ in
                    // Add new particle
                    let angle = Double.random(in: 0...2 * .pi)
                    let radius = 100.0
                    let centerX = geometry.size.width / 2
                    let centerY = geometry.size.height / 2
                    
                    let particle = RecordingParticle(
                        position: CGPoint(
                            x: centerX + cos(angle) * radius,
                            y: centerY + sin(angle) * radius
                        ),
                        opacity: 1,
                        scale: 1,
                        color: [.cyan, .blue, .purple].randomElement()!
                    )
                    particles.append(particle)
                    
                    // Animate particle
                    withAnimation(.easeOut(duration: 1)) {
                        if let index = particles.firstIndex(where: { $0.id == particle.id }) {
                            particles[index].position = CGPoint(
                                x: centerX + cos(angle) * radius * 1.5,
                                y: centerY + sin(angle) * radius * 1.5
                            )
                            particles[index].opacity = 0
                            particles[index].scale = 0.5
                        }
                    }
                    
                    // Remove old particles
                    particles = particles.suffix(20)
                }
            }
        }
    }
}

#Preview {
    ModernContentView()
}