//
//  AuthenticationManager.swift
//  cough
//
//  Created by Reid Glaze on 7/17/25.
//

import Foundation
import Firebase
import FirebaseAuth
import FirebaseFirestore

class AuthenticationManager: ObservableObject {
    @Published var user: User?
    @Published var isAuthenticated = false
    @Published var isLoading = false
    @Published var isAuthStateLoading = true
    @Published var errorMessage: String?
    
    private let auth = Auth.auth()
    private let db = Firestore.firestore()
    private var authStateListenerHandle: AuthStateDidChangeListenerHandle?
    
    init() {
        authStateListenerHandle = auth.addStateDidChangeListener { [weak self] _, user in
            DispatchQueue.main.async {
                self?.user = user
                self?.isAuthenticated = user != nil
                self?.isAuthStateLoading = false
            }
        }
    }
    
    deinit {
        if let handle = authStateListenerHandle {
            auth.removeStateDidChangeListener(handle)
        }
    }
    
    func signInAnonymously() async {
        await MainActor.run {
            isLoading = true
            errorMessage = nil
        }
        
        do {
            let authResult = try await auth.signInAnonymously()
            let uid = authResult.user.uid
            
            print("🔐 Successfully authenticated with UID: \(uid)")
            let app = db.app
            print("📍 Project ID: \(app.options.projectID ?? "Unknown")")
            
            let userData: [String: Any] = [
                "uid": uid,
                "createdAt": FieldValue.serverTimestamp(),
                "lastActiveAt": FieldValue.serverTimestamp(),
                "timezone": TimeZone.current.identifier,
                "platform": "iOS",
                "appVersion": Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
            ]
            
            print("📝 Attempting to write to Firestore...")
            print("📂 Collection: users")
            print("📄 Document ID: \(uid)")
            print("📊 User data to write: \(userData)")
            
            // Write the user data
            try await db.collection("users").document(uid).setData(userData)
            
            print("✅ Successfully wrote user data to Firestore")
            
            // Verify the write by reading it back
            let document = try await db.collection("users").document(uid).getDocument()
            if document.exists {
                print("✅ Verified: Document exists in Firestore")
                print("📊 Data: \(document.data() ?? [:])")
            } else {
                print("⚠️ Warning: Document not found after write")
            }
            
            await MainActor.run {
                isLoading = false
            }
        } catch {
            await MainActor.run {
                isLoading = false
                errorMessage = error.localizedDescription
            }
            print("❌ Error signing in anonymously: \(error)")
            print("📍 Error details: \(error.localizedDescription)")
        }
    }
    
    
    func signOut() {
        do {
            try auth.signOut()
        } catch {
            errorMessage = error.localizedDescription
            print("Error signing out: \(error)")
        }
    }
}