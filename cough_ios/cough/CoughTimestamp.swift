import Foundation

/// Converts a saved analysis `timestamp` into a `Date`.
///
/// The backend saves `timestamp` as milliseconds since 1970 (JavaScript
/// `Date.now()`), while the app's own fallback values are seconds. Reading a
/// millisecond value as seconds put dates in the year 57,000+. Every analysis
/// already saved keeps its stored value; only the reading of it changes here.
enum CoughTimestamp {
    /// Values at or above this cannot be seconds (as seconds it is the year
    /// 5138), so they are milliseconds (as milliseconds it is March 1973).
    static let millisecondsThreshold: Double = 100_000_000_000

    /// Seconds since 1970 for a stored value in either unit.
    static func seconds(from stored: Double) -> Double {
        stored >= millisecondsThreshold ? stored / 1000 : stored
    }

    static func date(from stored: Double) -> Date {
        Date(timeIntervalSince1970: seconds(from: stored))
    }
}
