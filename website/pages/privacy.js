import Head from 'next/head'
import Link from 'next/link'

export default function Privacy() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <Head>
        <title>Privacy Policy - Cough Checker</title>
      </Head>

      {/* Navigation */}
      <nav className="fixed top-0 w-full z-50 glass-effect">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <Link
              href="/"
              className="flex items-center text-gray-700 hover:text-blue-600 transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth="1.5"
                stroke="currentColor"
                aria-hidden="true"
                data-slot="icon"
                className="w-5 h-5 mr-2"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18"
                />
              </svg>
              <span className="font-medium">Back to Home</span>
            </Link>
            <h1 className="text-2xl font-bold gradient-text">Cough Checker</h1>
          </div>
        </div>
      </nav>

      {/* Content */}
      <div className="pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-3xl shadow-xl p-8 md:p-12">
            <h1 className="text-4xl font-bold text-gray-900 mb-8">Privacy Policy</h1>
            <div className="prose prose-lg max-w-none">
              <p className="text-gray-600 mb-6">
                <strong>Effective Date: February 20, 2026</strong>
              </p>
              <p className="text-gray-700 mb-8">
                TwinTip Solutions (&quot;we,&quot; &quot;our,&quot; or &quot;us&quot;) operates
                the Cough Checker mobile application (the &quot;App&quot;). This Privacy Policy
                describes how we collect, use, and protect your information when you use our
                App. By using Cough Checker, you agree to the collection and use of information
                in accordance with this policy.
              </p>

              {/* Section 1 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  1. Information We Collect
                </h2>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  1.1 Audio Data
                </h3>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    We collect audio recordings of your cough when you use the App&apos;s
                    analysis feature. The App requires microphone access solely for this
                    purpose, and the microphone is only active while you are recording a cough
                  </li>
                  <li>
                    Audio recordings are temporarily processed and are not permanently stored on
                    our servers
                  </li>
                  <li>
                    Audio data is converted to technical parameters for analysis and the
                    original recording is deleted immediately after processing
                  </li>
                </ul>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  1.2 Health Information
                </h3>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    Analysis results including cough type, severity assessment, and potential
                    causes
                  </li>
                  <li>Timestamp of when analyses were performed</li>
                  <li>
                    This information is stored locally on your device and in your secure
                    Firebase account
                  </li>
                </ul>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  1.3 Technical Information
                </h3>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>Anonymous user identifier (Firebase Authentication ID)</li>
                  <li>Device type and operating system version</li>
                  <li>App version</li>
                  <li>General geographic location (timezone only)</li>
                  <li>Error logs and performance data</li>
                </ul>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  1.4 Analytics and Usage Data
                </h3>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    We use Firebase Analytics (provided by Google) to collect anonymous usage
                    data such as app opens, screen views, session duration, and general
                    engagement metrics
                  </li>
                  <li>
                    Analytics data is used solely to understand how the App is used and to
                    improve the user experience
                  </li>
                  <li>
                    This data is collected automatically and does not include personally
                    identifiable information
                  </li>
                  <li>
                    You may disable analytics data collection through your device&apos;s
                    settings
                  </li>
                </ul>
              </section>

              {/* Section 2 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  2. How We Use Your Information
                </h2>
                <p className="text-gray-700 mb-4">
                  We use the collected information for the following purposes:
                </p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    <strong>To provide the service:</strong> Analyze your cough and provide
                    educational health insights
                  </li>
                  <li>
                    <strong>To maintain your history:</strong> Store your analysis results for
                    your personal reference
                  </li>
                  <li>
                    <strong>To improve the App:</strong> Understand usage patterns and improve
                    our AI models (using aggregated, anonymized data only)
                  </li>
                  <li>
                    <strong>To ensure security:</strong> Detect and prevent fraudulent or
                    harmful activities
                  </li>
                  <li>
                    <strong>To communicate:</strong> Send important updates about the App (if
                    you&apos;ve provided contact information)
                  </li>
                </ul>
              </section>

              {/* Section 3 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  3. Data Storage and Security
                </h2>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    Your data is stored using Google Firebase, which employs industry-standard
                    security measures
                  </li>
                  <li>All data transmission is encrypted using SSL/TLS protocols</li>
                  <li>
                    Audio recordings are processed in memory and immediately deleted after
                    analysis
                  </li>
                  <li>We do not store raw audio files on our servers</li>
                  <li>
                    Access to user data is strictly limited to authorized personnel only
                  </li>
                </ul>
              </section>

              {/* Section 4 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  4. Data Sharing and Third Parties
                </h2>
                <p className="text-gray-700 mb-4">
                  We do not sell, trade, or rent your personal information to third parties. We
                  may share your information only in the following circumstances:
                </p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    <strong>Service Providers:</strong> With Firebase (Google) for
                    authentication, data storage, and analytics
                  </li>
                  <li>
                    <strong>Legal Requirements:</strong> If required by law or to protect our
                    rights and safety
                  </li>
                  <li>
                    <strong>Business Transfers:</strong> In the event of a merger, acquisition,
                    or sale of assets
                  </li>
                  <li>
                    <strong>With Your Consent:</strong> When you explicitly agree to share your
                    information
                  </li>
                </ul>
                <p className="text-gray-700 mt-4">
                  <strong>Important:</strong> We never use your health data for advertising,
                  marketing, or data mining purposes.
                </p>
              </section>

              {/* Section 5 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  5. Your Rights and Choices
                </h2>
                <p className="text-gray-700 mb-4">
                  You have the following rights regarding your personal information:
                </p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    <strong>Access:</strong> View your stored analysis history within the App
                  </li>
                  <li>
                    <strong>Deletion:</strong> Delete individual analyses or your entire account
                    and all associated data
                  </li>
                  <li>
                    <strong>Portability:</strong> Export your analysis history (feature
                    availability may vary)
                  </li>
                  <li>
                    <strong>Correction:</strong> Report any inaccuracies in your data
                  </li>
                  <li>
                    <strong>Opt-out:</strong> Choose not to participate in anonymized data
                    aggregation for App improvement
                  </li>
                </ul>
              </section>

              {/* Section 6 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  6. Children&apos;s Privacy
                </h2>
                <p className="text-gray-700">
                  Cough Checker is not intended for use by children under 13 years of age. We
                  do not knowingly collect personal information from children under 13. If you
                  are a parent or guardian and believe your child has provided us with personal
                  information, please contact us immediately.
                </p>
              </section>

              {/* Section 7 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  7. International Data Transfers
                </h2>
                <p className="text-gray-700">
                  Your information may be transferred to and maintained on servers located
                  outside of your state, province, country, or other governmental jurisdiction.
                  By using the App, you consent to such transfers.
                </p>
              </section>

              {/* Section 8 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  8. AI and Machine Learning
                </h2>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    We do not use your individual data to train our AI models
                  </li>
                  <li>
                    Only aggregated, anonymized insights may be used to improve our algorithms
                  </li>
                  <li>
                    You can opt-out of contributing to aggregated insights in the App settings
                  </li>
                </ul>
              </section>

              {/* Section 9 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  9. Health Data Special Provisions
                </h2>
                <p className="text-gray-700 mb-4">
                  As a health-related app, we adhere to strict guidelines regarding health
                  information:
                </p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    Health data is never used for advertising or marketing purposes
                  </li>
                  <li>
                    We do not share health data with insurance companies or employers
                  </li>
                  <li>All health insights provided are for educational purposes only</li>
                  <li>
                    We encourage users to consult healthcare professionals for medical advice
                  </li>
                </ul>
              </section>

              {/* Section 10 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  10. Data Retention
                </h2>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    Analysis results are retained as long as you maintain your account
                  </li>
                  <li>
                    Deleted analyses are permanently removed from our systems within 30 days
                  </li>
                  <li>
                    Account deletion results in permanent removal of all associated data within
                    90 days
                  </li>
                  <li>
                    Some anonymized, aggregated data may be retained for App improvement
                    purposes
                  </li>
                </ul>
              </section>

              {/* Section 11 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  11. Changes to This Privacy Policy
                </h2>
                <p className="text-gray-700">
                  We may update our Privacy Policy from time to time. We will notify you of any
                  changes by posting the new Privacy Policy on this page and updating the
                  &quot;Effective Date&quot; at the top. You are advised to review this Privacy
                  Policy periodically for any changes.
                </p>
              </section>

              {/* Section 12 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  12. California Privacy Rights
                </h2>
                <p className="text-gray-700">
                  If you are a California resident, you have additional rights under the
                  California Consumer Privacy Act (CCPA), including:
                </p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    The right to know what personal information we collect, use, disclose, and
                    sell
                  </li>
                  <li>
                    The right to request deletion of your personal information
                  </li>
                  <li>
                    The right to opt-out of the sale of personal information (we do not sell
                    personal information)
                  </li>
                  <li>
                    The right to non-discrimination for exercising your privacy rights
                  </li>
                </ul>
              </section>

              {/* Section 13 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  13. European Privacy Rights (GDPR)
                </h2>
                <p className="text-gray-700 mb-4">
                  If you are located in the European Economic Area (EEA) or the United Kingdom,
                  you have additional rights under the General Data Protection Regulation
                  (GDPR), including:
                </p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>
                    The right to access, rectify, or erase your personal data
                  </li>
                  <li>
                    The right to restrict or object to the processing of your personal data
                  </li>
                  <li>The right to data portability</li>
                  <li>The right to withdraw consent at any time</li>
                  <li>
                    The right to lodge a complaint with a supervisory authority
                  </li>
                </ul>
                <p className="text-gray-700 mt-4">
                  Our legal basis for processing your data includes: your consent (for audio
                  recording and analytics), the performance of our service (for cough
                  analysis), and our legitimate interests (for App improvement and security).
                </p>
              </section>

              {/* Section 14 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">14. Contact Us</h2>
                <p className="text-gray-700 mb-4">
                  If you have any questions about this Privacy Policy or our data practices,
                  please contact us at:
                </p>
                <div className="bg-gray-50 rounded-lg p-6">
                  <p className="text-gray-700">
                    <strong>TwinTip Solutions</strong>
                    <br />
                    Email: reid@twintipsolutions.com
                  </p>
                </div>
              </section>

              {/* Medical Disclaimer */}
              <section className="mt-12 p-6 bg-blue-50 rounded-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Medical Disclaimer
                </h3>
                <p className="text-gray-700 text-sm">
                  Cough Checker provides educational information only and is not a substitute
                  for professional medical advice, diagnosis, or treatment. Always seek the
                  advice of your physician or other qualified health provider with any questions
                  you may have regarding a medical condition.
                </p>
              </section>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
