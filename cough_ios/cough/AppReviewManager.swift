//
//  AppReviewManager.swift
//  cough
//
//  Created by Reid Glaze on 1/24/26.
//

import StoreKit
import SwiftUI
import UIKit

class AppReviewManager: ObservableObject {
    static let shared = AppReviewManager()

    private let userDefaults = UserDefaults.standard

    // Keys for UserDefaults
    private let analysisCountKey = "appReviewAnalysisCount"
    private let historyViewCountKey = "appReviewHistoryViewCount"
    private let lastReviewRequestDateKey = "appReviewLastRequestDate"
    private let reviewRequestCountKey = "appReviewRequestCount"

    // Thresholds for requesting review
    private let analysisThreshold = 2  // Request after 2 analyses
    private let historyViewThreshold = 3  // Or after viewing history 3 times
    private let minimumDaysBetweenRequests = 14  // Don't ask more than once every 14 days

    private init() {}

    // MARK: - Public Methods

    /// Call this after each successful cough analysis
    func incrementAnalysisCount() {
        let currentCount = userDefaults.integer(forKey: analysisCountKey)
        let newCount = currentCount + 1
        userDefaults.set(newCount, forKey: analysisCountKey)

        // Check if we should request a review
        if newCount >= analysisThreshold && newCount % analysisThreshold == 0 {
            requestReviewIfAppropriate()
        }
    }

    /// Call this when user views their history
    func incrementHistoryViewCount() {
        let currentCount = userDefaults.integer(forKey: historyViewCountKey)
        let newCount = currentCount + 1
        userDefaults.set(newCount, forKey: historyViewCountKey)

        // Check if we should request a review (engaged users who check history)
        if newCount >= historyViewThreshold && newCount % historyViewThreshold == 0 {
            requestReviewIfAppropriate()
        }
    }

    /// Manual request from settings/about screen
    func requestReviewManually() {
        requestReview()
    }

    // MARK: - Private Methods

    private func requestReviewIfAppropriate() {
        // Check if enough time has passed since last request
        if let lastRequestDate = userDefaults.object(forKey: lastReviewRequestDateKey) as? Date {
            let daysSinceLastRequest = Calendar.current.dateComponents([.day], from: lastRequestDate, to: Date()).day ?? 0

            if daysSinceLastRequest < minimumDaysBetweenRequests {
                return
            }
        }

        // Check total analysis count - only ask users who have used the app meaningfully
        let totalAnalyses = userDefaults.integer(forKey: analysisCountKey)
        if totalAnalyses < analysisThreshold {
            return
        }

        requestReview()
    }

    private func requestReview() {
        // Update tracking
        userDefaults.set(Date(), forKey: lastReviewRequestDateKey)
        let requestCount = userDefaults.integer(forKey: reviewRequestCountKey)
        userDefaults.set(requestCount + 1, forKey: reviewRequestCountKey)

        // Request the review
        DispatchQueue.main.async {
            if let windowScene = UIApplication.shared.connectedScenes
                .compactMap({ $0 as? UIWindowScene })
                .first(where: { $0.activationState == .foregroundActive }) {
                SKStoreReviewController.requestReview(in: windowScene)
            }
        }
    }

    // MARK: - Debug/Testing

    /// Reset all counters (useful for testing)
    func resetAllCounters() {
        userDefaults.removeObject(forKey: analysisCountKey)
        userDefaults.removeObject(forKey: historyViewCountKey)
        userDefaults.removeObject(forKey: lastReviewRequestDateKey)
        userDefaults.removeObject(forKey: reviewRequestCountKey)
    }

    /// Get current stats (for debugging)
    var debugStats: String {
        let analyses = userDefaults.integer(forKey: analysisCountKey)
        let historyViews = userDefaults.integer(forKey: historyViewCountKey)
        let requests = userDefaults.integer(forKey: reviewRequestCountKey)
        let lastDate = userDefaults.object(forKey: lastReviewRequestDateKey) as? Date

        return """
        Analyses: \(analyses)
        History Views: \(historyViews)
        Review Requests: \(requests)
        Last Request: \(lastDate?.description ?? "Never")
        """
    }
}
