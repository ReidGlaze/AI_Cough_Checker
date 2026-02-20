import Head from 'next/head'
import Link from 'next/link'

export default function Terms() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <Head>
        <title>Terms of Service - Cough Checker</title>
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
            <h1 className="text-4xl font-bold text-gray-900 mb-8">Terms of Service</h1>
            <div className="prose prose-lg max-w-none">
              <p className="text-gray-600 mb-6">
                <strong>Effective Date: January 20, 2025</strong>
              </p>

              {/* Section 1 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  1. Acceptance of Terms
                </h2>
                <p className="text-gray-700">
                  By downloading, installing, or using the Cough Checker mobile application
                  (&quot;App&quot;), you agree to be bound by these Terms of Service
                  (&quot;Terms&quot;). If you do not agree to these Terms, do not use the App.
                </p>
              </section>

              {/* Section 2 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  2. Medical Disclaimer
                </h2>
                <div className="bg-red-50 border-l-4 border-red-500 p-4 my-4">
                  <p className="text-gray-700 font-semibold">
                    IMPORTANT: Cough Checker is for educational and informational purposes only.
                    It is NOT a substitute for professional medical advice, diagnosis, or
                    treatment. Always seek the advice of your physician or other qualified
                    health provider with any questions you may have regarding a medical
                    condition. Never disregard professional medical advice or delay in seeking it
                    because of something you have read or learned through this App.
                  </p>
                </div>
              </section>

              {/* Section 3 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">3. Use of the App</h2>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  3.1 Eligibility
                </h3>
                <p className="text-gray-700 mb-4">
                  You must be at least 13 years old to use this App. By using the App, you
                  represent and warrant that you meet this age requirement.
                </p>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  3.2 License
                </h3>
                <p className="text-gray-700 mb-4">
                  We grant you a limited, non-exclusive, non-transferable, revocable license to
                  use the App for your personal, non-commercial use.
                </p>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  3.3 Restrictions
                </h3>
                <p className="text-gray-700 mb-2">You agree not to:</p>
                <ul className="list-disc pl-6 space-y-2 text-gray-700">
                  <li>Use the App for any illegal or unauthorized purpose</li>
                  <li>
                    Attempt to reverse engineer, decompile, or disassemble the App
                  </li>
                  <li>Remove or alter any proprietary notices or labels</li>
                  <li>Use the App to transmit any harmful or malicious code</li>
                  <li>Interfere with or disrupt the App or servers</li>
                  <li>
                    Use the App for commercial purposes without our written consent
                  </li>
                </ul>
              </section>

              {/* Section 4 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  4. Privacy and Data Use
                </h2>
                <p className="text-gray-700">
                  Your use of the App is also governed by our Privacy Policy. By using the App,
                  you consent to the collection and use of your information as described in our
                  Privacy Policy.
                </p>
              </section>

              {/* Section 5 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  5. Intellectual Property
                </h2>
                <p className="text-gray-700">
                  All content, features, and functionality of the App, including but not limited
                  to text, graphics, logos, and software, are the exclusive property of TwinTip
                  Solutions and are protected by international copyright, trademark, and other
                  intellectual property laws.
                </p>
              </section>

              {/* Section 6 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  6. Disclaimers and Limitations of Liability
                </h2>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  6.1 No Medical Advice
                </h3>
                <p className="text-gray-700 mb-4">
                  The App does not provide medical advice. The information provided is for
                  educational purposes only and should not be used for diagnosing or treating
                  health problems or diseases.
                </p>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  6.2 Accuracy
                </h3>
                <p className="text-gray-700 mb-4">
                  While we strive to provide accurate information, we make no representations or
                  warranties about the accuracy, reliability, completeness, or timeliness of any
                  information provided by the App.
                </p>

                <h3 className="text-xl font-semibold text-gray-800 mt-6 mb-3">
                  6.3 Limitation of Liability
                </h3>
                <p className="text-gray-700">
                  TO THE MAXIMUM EXTENT PERMITTED BY LAW, TWINTIP SOLUTIONS SHALL NOT BE LIABLE
                  FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES, OR
                  ANY LOSS OF PROFITS OR REVENUES, WHETHER INCURRED DIRECTLY OR INDIRECTLY, OR
                  ANY LOSS OF DATA, USE, GOODWILL, OR OTHER INTANGIBLE LOSSES RESULTING FROM
                  YOUR USE OF THE APP.
                </p>
              </section>

              {/* Section 7 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  7. Indemnification
                </h2>
                <p className="text-gray-700">
                  You agree to indemnify, defend, and hold harmless TwinTip Solutions and its
                  officers, directors, employees, and agents from and against any claims,
                  liabilities, damages, losses, and expenses arising out of or in any way
                  connected with your access to or use of the App.
                </p>
              </section>

              {/* Section 8 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  8. Modifications to the App and Terms
                </h2>
                <p className="text-gray-700">
                  We reserve the right to modify or discontinue the App at any time without
                  notice. We also reserve the right to modify these Terms at any time. Your
                  continued use of the App after any such changes constitutes your acceptance of
                  the new Terms.
                </p>
              </section>

              {/* Section 9 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">9. Termination</h2>
                <p className="text-gray-700">
                  We may terminate or suspend your access to the App immediately, without prior
                  notice or liability, for any reason whatsoever, including without limitation if
                  you breach the Terms.
                </p>
              </section>

              {/* Section 10 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  10. Governing Law
                </h2>
                <p className="text-gray-700">
                  These Terms shall be governed by and construed in accordance with the laws of
                  the United States, without regard to its conflict of law provisions. You agree
                  to submit to the personal and exclusive jurisdiction of the courts located
                  within the United States.
                </p>
              </section>

              {/* Section 11 */}
              <section className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  11. Contact Information
                </h2>
                <p className="text-gray-700 mb-4">
                  If you have any questions about these Terms, please contact us at:
                </p>
                <div className="bg-gray-50 rounded-lg p-6">
                  <p className="text-gray-700">
                    <strong>TwinTip Solutions</strong>
                    <br />
                    Email: reid@twintipsolutions.com
                  </p>
                </div>
              </section>

              {/* Emergency Notice */}
              <section className="mt-12 p-6 bg-yellow-50 rounded-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Emergency Medical Attention
                </h3>
                <p className="text-gray-700 text-sm">
                  If you think you may have a medical emergency, call your doctor, go to the
                  emergency department, or call emergency services immediately. Do not rely on
                  electronic communications or the App for urgent medical needs.
                </p>
              </section>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
