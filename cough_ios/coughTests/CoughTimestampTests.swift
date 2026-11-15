import XCTest
@testable import cough

final class CoughTimestampTests: XCTestCase {
    private func year(_ date: Date) -> Int {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(identifier: "UTC")!
        return calendar.component(.year, from: date)
    }

    func testMillisecondsFromBackendGiveTheRealDate() {
        // 2025-09-17T00:00:00Z as the backend stores it (Date.now()).
        let date = CoughTimestamp.date(from: 1_758_067_200_000)
        XCTAssertEqual(date.timeIntervalSince1970, 1_758_067_200, accuracy: 0.001)
        XCTAssertEqual(year(date), 2025)
    }

    func testOldBugWouldHaveShownYear57000() {
        let wrong = Date(timeIntervalSince1970: 1_758_067_200_000)
        XCTAssertGreaterThan(year(wrong), 57_000)
    }

    func testSecondsStayAsTheyAre() {
        let date = CoughTimestamp.date(from: 1_758_067_200)
        XCTAssertEqual(date.timeIntervalSince1970, 1_758_067_200, accuracy: 0.001)
        XCTAssertEqual(year(date), 2025)
    }

    func testFractionalSecondsStayAsTheyAre() {
        XCTAssertEqual(CoughTimestamp.seconds(from: 1_758_067_200.25), 1_758_067_200.25, accuracy: 0.0001)
    }

    func testOldestSavedHistoryInMilliseconds() {
        // 2025-07-01T00:00:00Z, before the app's first release.
        XCTAssertEqual(year(CoughTimestamp.date(from: 1_751_328_000_000)), 2025)
    }

    func testZeroFallbackIsUnchanged() {
        XCTAssertEqual(CoughTimestamp.seconds(from: 0), 0)
    }

    func testResultExposesNormalizedDateAndKeepsStoredValue() {
        let result = CoughAnalysisResult(
            analysisId: "a", timestamp: 1_758_067_200_000, coughType: "dry", severity: "mild",
            characteristics: [], potentialCauses: [], managementApproaches: [], urgency: "routine",
            confidence: 0.8, soundPattern: "", frequency: "", duration: "", additionalNotes: []
        )
        XCTAssertEqual(year(result.date), 2025)
        XCTAssertEqual(result.timestamp, 1_758_067_200_000)
    }
}
