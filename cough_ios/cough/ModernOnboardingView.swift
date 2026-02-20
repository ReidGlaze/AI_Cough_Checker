//
//  ModernOnboardingView.swift
//  cough
//
//  Created by Reid Glaze on 7/17/25.
//

import SwiftUI

struct ModernOnboardingView: View {
    @StateObject private var authManager = AuthenticationManager()
    @State private var currentPage = 0
    @State private var animateGradient = false
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    
    let totalPages = 5
    
    var body: some View {
        ZStack {
            AnimatedGradientBackground()
            
            TabView(selection: $currentPage) {
                ModernWelcomeView(authManager: authManager, currentPage: $currentPage)
                    .tag(0)
                
                ModernAboutView(currentPage: $currentPage)
                    .tag(1)
                
                ModernHowItWorksView(currentPage: $currentPage)
                    .tag(2)
                
                ModernPrivacyView(currentPage: $currentPage)
                    .tag(3)
                
                ModernGetStartedView(authManager: authManager, hasCompletedOnboarding: $hasCompletedOnboarding)
                    .tag(4)
            }
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            
            VStack {
                Spacer()
                CustomProgressIndicator(currentPage: $currentPage, totalPages: totalPages)
                    .padding(.bottom, 30)
            }
            .ignoresSafeArea()
        }
        .preferredColorScheme(.dark)
        .onAppear {
            print("ModernOnboardingView appeared")
        }
    }
}

struct AnimatedGradientBackground: View {
    @State private var animateGradient = false
    
    var body: some View {
        LinearGradient(
            colors: [
                Color(red: 0.1, green: 0.2, blue: 0.45),
                Color(red: 0.2, green: 0.1, blue: 0.4),
                Color(red: 0.15, green: 0.25, blue: 0.5),
                Color(red: 0.05, green: 0.15, blue: 0.35)
            ],
            startPoint: animateGradient ? .topLeading : .bottomLeading,
            endPoint: animateGradient ? .bottomTrailing : .topTrailing
        )
        .ignoresSafeArea()
        .onAppear {
            withAnimation(.easeInOut(duration: 8).repeatForever(autoreverses: true)) {
                animateGradient.toggle()
            }
        }
        .overlay(
            GeometryReader { geometry in
                ForEach(0..<3) { index in
                    Circle()
                        .fill(
                            RadialGradient(
                                colors: [
                                    Color.blue.opacity(0.3),
                                    Color.purple.opacity(0.2),
                                    Color.clear
                                ],
                                center: .center,
                                startRadius: 0,
                                endRadius: 150
                            )
                        )
                        .frame(width: 300, height: 300)
                        .blur(radius: 30)
                        .position(
                            x: CGFloat.random(in: 0...geometry.size.width),
                            y: CGFloat.random(in: 0...geometry.size.height)
                        )
                        .animation(
                            Animation.easeInOut(duration: Double.random(in: 15...25))
                                .repeatForever(autoreverses: true),
                            value: animateGradient
                        )
                }
            }
        )
    }
}


struct ModernWelcomeView: View {
    @ObservedObject var authManager: AuthenticationManager
    @Binding var currentPage: Int
    @State private var showElements = false
    @State private var pulseAnimation = false
    @State private var hasStartedAuth = false
    
    var body: some View {
        VStack(spacing: 40) {
            Spacer()
            
            ZStack {
                Circle()
                    .fill(
                        RadialGradient(
                            colors: [.blue.opacity(0.5), .purple.opacity(0.3), .clear],
                            center: .center,
                            startRadius: 0,
                            endRadius: 100
                        )
                    )
                    .frame(width: 200, height: 200)
                    .blur(radius: 20)
                    .scaleEffect(pulseAnimation ? 1.2 : 0.8)
                    .animation(.easeInOut(duration: 2).repeatForever(autoreverses: true), value: pulseAnimation)
                
                Image(systemName: "waveform.path.ecg")
                    .font(.system(size: 80))
                    .foregroundStyle(
                        LinearGradient(
                            colors: [.blue, .purple],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .scaleEffect(showElements ? 1 : 0.5)
                    .opacity(showElements ? 1 : 0)
            }
            
            VStack(spacing: 20) {
                Text("Cough Checker")
                    .font(.system(size: 48, weight: .bold, design: .rounded))
                    .foregroundStyle(
                        LinearGradient(
                            colors: [.white, .white.opacity(0.8)],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )
                    .opacity(showElements ? 1 : 0)
                    .offset(y: showElements ? 0 : 20)
                
                Text("AI-Powered Health Insights")
                    .font(.title3)
                    .foregroundColor(.white.opacity(0.8))
                    .opacity(showElements ? 1 : 0)
                    .offset(y: showElements ? 0 : 20)
            }
            
            Spacer()
            
            VStack(spacing: 20) {
                Text("Swipe to continue")
                    .font(.headline)
                    .foregroundColor(.white.opacity(0.7))
                    .opacity(showElements ? 1 : 0)
                    .offset(y: showElements ? 0 : 20)
                
                Image(systemName: "chevron.right.circle")
                    .font(.system(size: 40))
                    .foregroundColor(.white.opacity(0.5))
                    .opacity(showElements ? 1 : 0)
                    .offset(y: showElements ? 0 : 20)
            }
            .padding(.bottom, 100)
            
            if let error = authManager.errorMessage {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                    .padding(.bottom, 20)
            }
            
            Spacer()
                .frame(height: 80)
        }
        .onAppear {
            pulseAnimation = true
            withAnimation(.easeOut(duration: 1)) {
                showElements = true
            }
            
            if !hasStartedAuth && !authManager.isAuthenticated {
                hasStartedAuth = true
                print("🚀 Starting authentication process...")
                Task {
                    print("⏳ Waiting 1.5 seconds before authentication...")
                    try? await Task.sleep(nanoseconds: 1_500_000_000)
                    print("🔑 Starting anonymous sign in...")
                    await authManager.signInAnonymously()
                }
            }
        }
    }
}

struct ModernAboutView: View {
    @Binding var currentPage: Int
    @State private var showFeatures = false
    
    let features = [
        ("mic.circle.fill", "Advanced Audio Recording", "Capture high-quality cough samples"),
        ("brain", "AI Analysis", "Powered by cutting-edge machine learning"),
        ("chart.xyaxis.line", "Pattern Tracking", "Monitor your health trends over time"),
        ("doc.text.magnifyingglass", "Detailed Insights", "Educational health information and patterns")
    ]
    
    var body: some View {
        VStack(spacing: 25) {
            Text("Discover Features")
                .font(.system(size: 42, weight: .bold, design: .rounded))
                .foregroundColor(.white)
                .padding(.top, 60)
            
            ScrollView {
                VStack(spacing: 20) {
                    ForEach(Array(features.enumerated()), id: \.offset) { index, feature in
                        FeatureCard(
                            icon: feature.0,
                            title: feature.1,
                            description: feature.2,
                            delay: Double(index) * 0.1
                        )
                        .opacity(showFeatures ? 1 : 0)
                        .offset(x: showFeatures ? 0 : -50)
                        .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(Double(index) * 0.1), value: showFeatures)
                    }
                }
                .padding(.horizontal, 30)
            }
            
            Spacer()
                .frame(height: 80)
        }
        .onAppear {
            withAnimation {
                showFeatures = true
            }
        }
    }
}

struct ModernHowItWorksView: View {
    @Binding var currentPage: Int
    @State private var currentStep = 0
    @State private var timer: Timer?
    
    let steps = [
        ("1", "Record", "Tap and cough naturally", "mic.circle.fill", Color.blue),
        ("2", "Analyze", "AI processes your audio", "waveform", Color.purple),
        ("3", "Results", "View detailed insights", "chart.bar.fill", Color.green)
    ]
    
    var body: some View {
        VStack {
            Text("How It Works")
                .font(.system(size: 42, weight: .bold, design: .rounded))
                .foregroundColor(.white)
                .padding(.top, 80)
                .padding(.bottom, 80)
            
            ZStack {
                ForEach(Array(steps.enumerated()), id: \.offset) { index, step in
                    StepCard(
                        number: step.0,
                        title: step.1,
                        description: step.2,
                        icon: step.3,
                        color: step.4,
                        isActive: currentStep == index
                    )
                    .offset(y: CGFloat(index - currentStep) * 20)
                    .scaleEffect(currentStep == index ? 1 : 0.9)
                    .opacity(currentStep == index ? 1 : 0.3)
                    .zIndex(currentStep == index ? 1 : 0)
                }
            }
            .padding(.horizontal, 40)
            .onAppear {
                // ALWAYS reset to step 1 when page appears
                withAnimation(.none) {
                    currentStep = 0
                }
                
                // Cancel any existing timer first
                timer?.invalidate()
                timer = nil
                
                // Small delay to ensure reset happens first
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    // Create a new timer
                    timer = Timer.scheduledTimer(withTimeInterval: 2.5, repeats: true) { _ in
                        withAnimation(.spring(response: 0.6, dampingFraction: 0.8)) {
                            currentStep = (currentStep + 1) % steps.count
                        }
                    }
                }
            }
            .onDisappear {
                // Clean up timer when view disappears
                timer?.invalidate()
                timer = nil
                // Reset step when leaving
                currentStep = 0
            }
            
            Spacer()
        }
    }
}

struct ModernPrivacyView: View {
    @Binding var currentPage: Int
    @State private var showItems = false
    
    let privacyFeatures = [
        ("lock.shield.fill", "Secure Cloud Processing", Color.purple),
        ("person.crop.circle.badge.xmark", "Anonymous by Default", Color.blue),
        ("brain.head.profile", "AI Doesn't Train on Your Data", Color.green),
        ("trash.circle.fill", "Delete Anytime", Color.red)
    ]
    
    var body: some View {
        VStack(spacing: 25) {
            Spacer()
                .frame(height: 40)
            
            ZStack {
                Circle()
                    .fill(
                        RadialGradient(
                            colors: [.purple.opacity(0.5), .blue.opacity(0.3), .clear],
                            center: .center,
                            startRadius: 0,
                            endRadius: 100
                        )
                    )
                    .frame(width: 150, height: 150)
                    .blur(radius: 20)
                
                Image(systemName: "lock.shield.fill")
                    .font(.system(size: 80))
                    .foregroundStyle(
                        LinearGradient(
                            colors: [.purple, .blue],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .rotationEffect(.degrees(showItems ? 0 : -30))
            }
            
            Text("Your Privacy First")
                .font(.system(size: 38, weight: .bold, design: .rounded))
                .foregroundColor(.white)
            
            VStack(spacing: 20) {
                ForEach(Array(privacyFeatures.enumerated()), id: \.offset) { index, feature in
                    PrivacyFeatureRow(
                        icon: feature.0,
                        text: feature.1,
                        color: feature.2
                    )
                    .opacity(showItems ? 1 : 0)
                    .offset(x: showItems ? 0 : 50)
                    .animation(.spring().delay(Double(index) * 0.1), value: showItems)
                }
            }
            .padding(.horizontal, 40)
            
            Spacer()
        }
        .onAppear {
            withAnimation {
                showItems = true
            }
        }
    }
}

struct ModernGetStartedView: View {
    @ObservedObject var authManager: AuthenticationManager
    @Binding var hasCompletedOnboarding: Bool
    @State private var showContent = false
    @State private var particlesVisible = false
    
    var body: some View {
        VStack(spacing: 30) {
            Spacer()
                .frame(height: 40)
            
            ZStack {
                if particlesVisible {
                    ParticleEffectView()
                }
                
                Image(systemName: "checkmark.seal.fill")
                    .font(.system(size: 100))
                    .foregroundStyle(
                        LinearGradient(
                            colors: [.green, .mint],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .scaleEffect(showContent ? 1 : 0)
                    .rotationEffect(.degrees(showContent ? 0 : 180))
            }
            
            VStack(spacing: 20) {
                Text("Ready to Begin!")
                    .font(.system(size: 42, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
                
                Text("Your journey to better health awareness starts now")
                    .font(.title3)
                    .foregroundColor(.white.opacity(0.8))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }
            .opacity(showContent ? 1 : 0)
            .offset(y: showContent ? 0 : 30)
            
            Spacer()
            
            Button(action: {
                hasCompletedOnboarding = true
            }) {
                ZStack {
                    GlassmorphicCard()
                        .frame(height: 60)
                    
                    HStack {
                        Text("Start Using Cough Checker")
                            .font(.headline)
                            .foregroundColor(.white)
                        
                        Image(systemName: "arrow.right")
                            .font(.headline)
                            .foregroundColor(.white)
                    }
                }
            }
            .padding(.horizontal, 30)
            .opacity(showContent ? 1 : 0)
            .offset(y: showContent ? 0 : 50)
            
            Spacer()
                .frame(height: 80)
        }
        .onAppear {
            withAnimation(.spring(response: 0.8, dampingFraction: 0.6)) {
                showContent = true
            }
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                particlesVisible = true
            }
        }
    }
}

struct FeatureCard: View {
    let icon: String
    let title: String
    let description: String
    let delay: Double
    
    var body: some View {
        ZStack {
            GlassmorphicCard()
            
            HStack(spacing: 20) {
                Image(systemName: icon)
                    .font(.system(size: 40))
                    .foregroundStyle(
                        LinearGradient(
                            colors: [.blue, .purple],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .frame(width: 60)
                
                VStack(alignment: .leading, spacing: 8) {
                    Text(title)
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    Text(description)
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.7))
                        .lineLimit(2)
                }
                
                Spacer()
            }
            .padding(20)
        }
    }
}

struct StepCard: View {
    let number: String
    let title: String
    let description: String
    let icon: String
    let color: Color
    let isActive: Bool
    
    var body: some View {
        ZStack {
            GlassmorphicCard()
            
            VStack(spacing: 20) {
                ZStack {
                    Circle()
                        .fill(color.opacity(0.2))
                        .frame(width: 80, height: 80)
                    
                    Image(systemName: icon)
                        .font(.system(size: 40))
                        .foregroundColor(color)
                }
                
                VStack(spacing: 10) {
                    Text("Step \(number)")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.7))
                    
                    Text(title)
                        .font(.title2.bold())
                        .foregroundColor(.white)
                    
                    Text(description)
                        .font(.body)
                        .foregroundColor(.white.opacity(0.8))
                        .multilineTextAlignment(.center)
                }
            }
            .padding(30)
        }
        .frame(height: 300)
    }
}

struct PrivacyFeatureRow: View {
    let icon: String
    let text: String
    let color: Color
    
    var body: some View {
        HStack(spacing: 20) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(color)
                .frame(width: 40)
            
            Text(text)
                .font(.body)
                .foregroundColor(.white)
            
            Spacer()
        }
        .padding(.vertical, 10)
    }
}

struct GetStartedButton: View {
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            ZStack {
                RoundedRectangle(cornerRadius: 16)
                    .fill(
                        LinearGradient(
                            colors: [.blue, .purple],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .frame(height: 60)
                
                Text("Get Started")
                    .font(.headline)
                    .foregroundColor(.white)
            }
        }
    }
}

struct ContinueButton: View {
    let title: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            ZStack {
                GlassmorphicCard()
                    .frame(height: 60)
                
                HStack {
                    Text(title)
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    Image(systemName: "arrow.right")
                        .font(.headline)
                        .foregroundColor(.white)
                }
            }
        }
    }
}

struct CustomProgressIndicator: View {
    @Binding var currentPage: Int
    let totalPages: Int
    
    var body: some View {
        HStack(spacing: 8) {
            ForEach(0..<totalPages, id: \.self) { page in
                Capsule()
                    .fill(currentPage == page ? Color.white : Color.white.opacity(0.3))
                    .frame(width: currentPage == page ? 30 : 10, height: 10)
                    .animation(.spring(), value: currentPage)
            }
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 10)
        .background(
            Capsule()
                .fill(.ultraThinMaterial)
        )
    }
}

struct ParticleEffectView: View {
    @State private var particles: [Particle] = []
    
    struct Particle: Identifiable {
        let id = UUID()
        var position: CGPoint
        var opacity: Double
        var scale: CGFloat
    }
    
    var body: some View {
        GeometryReader { geometry in
            ZStack {
                ForEach(particles) { particle in
                    Image(systemName: "sparkle")
                        .foregroundColor(.white)
                        .opacity(particle.opacity)
                        .scaleEffect(particle.scale)
                        .position(particle.position)
                }
            }
            .onAppear {
                for _ in 0..<20 {
                    let particle = Particle(
                        position: CGPoint(
                            x: CGFloat.random(in: 0...geometry.size.width),
                            y: CGFloat.random(in: 0...geometry.size.height)
                        ),
                        opacity: Double.random(in: 0.3...1),
                        scale: CGFloat.random(in: 0.5...1.5)
                    )
                    particles.append(particle)
                }
                
                let width = geometry.size.width
                let height = geometry.size.height
                
                Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { _ in
                    for index in particles.indices {
                        withAnimation(.easeOut(duration: 2)) {
                            particles[index].opacity = Double.random(in: 0.1...1)
                            particles[index].position.y -= CGFloat.random(in: 1...5)
                            
                            if particles[index].position.y < 0 {
                                particles[index].position.y = height
                                particles[index].position.x = CGFloat.random(in: 0...width)
                            }
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    ModernOnboardingView()
}