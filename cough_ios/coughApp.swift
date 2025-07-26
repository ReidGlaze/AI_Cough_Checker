//
//  coughApp.swift
//  cough
//
//  Created by Reid Glaze on 7/17/25.
//

import SwiftUI
import Firebase
import FirebaseFirestore
import FirebaseFunctions

@main
struct coughApp: App {
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @StateObject private var authManager = AuthenticationManager()
    
    init() {
        print("coughApp init started")
        FirebaseApp.configure()
        
        print("Firebase configured successfully")
        if let app = FirebaseApp.app() {
            print("Project ID: \(app.options.projectID ?? "Unknown")")
        }
        
        // Debug: Check onboarding status
        print("Current hasCompletedOnboarding value: \(UserDefaults.standard.bool(forKey: "hasCompletedOnboarding"))")
        
        print("coughApp init completed")
    }
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                // Show a loading screen while Firebase initializes
                if authManager.isAuthStateLoading {
                    ZStack {
                        LinearGradient(
                            colors: [
                                Color(red: 0.05, green: 0.1, blue: 0.2),
                                Color(red: 0.1, green: 0.15, blue: 0.25)
                            ],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                        .ignoresSafeArea()
                        
                        VStack(spacing: 20) {
                            Image(systemName: "waveform.path.ecg.rectangle")
                                .font(.system(size: 60))
                                .foregroundColor(.cyan)
                            
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                .scaleEffect(1.2)
                        }
                    }
                } else {
                    Group {
                        if hasCompletedOnboarding && authManager.user != nil {
                            ModernContentView()
                                .environmentObject(authManager)
                                .onAppear {
                                    print("Showing ModernContentView")
                                }
                        } else {
                            ModernOnboardingView()
                                .environmentObject(authManager)
                                .onAppear {
                                    print("Showing ModernOnboardingView")
                                }
                        }
                    }
                }
            }
            .onAppear {
                print("App WindowGroup appeared, hasCompletedOnboarding: \(hasCompletedOnboarding)")
            }
        }
    }
}
