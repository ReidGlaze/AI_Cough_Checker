import Head from 'next/head'
import Link from 'next/link'

const StarIcon = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="currentColor"
    aria-hidden="true"
    data-slot="icon"
    className="w-5 h-5 text-yellow-400"
  >
    <path
      fillRule="evenodd"
      d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.006 5.404.434c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.434 2.082-5.005Z"
      clipRule="evenodd"
    />
  </svg>
)

const CheckIcon = () => (
  <svg className="w-5 h-5 text-green-500 mr-3" fill="currentColor" viewBox="0 0 20 20">
    <path
      fillRule="evenodd"
      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
      clipRule="evenodd"
    />
  </svg>
)

const AppleIcon = ({ className }) => (
  <svg className={className} viewBox="0 0 24 24" fill="currentColor">
    <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-.48-3.24 0-1.44.62-2.2.44-3.06-.35C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.38 5.98.48 7.13-.57 1.5-1.31 2.99-2.54 4.09l.01-.01zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.51-3.74 4.25z" />
  </svg>
)

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <Head>
        <title>Cough Checker - AI-Powered Cough Analysis App</title>
      </Head>

      {/* Navigation */}
      <nav className="fixed top-0 w-full z-50 glass-effect">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <h1 className="text-2xl font-bold gradient-text">Cough Checker</h1>
              </div>
            </div>
            <div className="hidden md:block">
              <div className="ml-10 flex items-baseline space-x-8">
                <a
                  href="#features"
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 text-sm font-medium transition-colors"
                >
                  Features
                </a>
                <a
                  href="#how-it-works"
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 text-sm font-medium transition-colors"
                >
                  How It Works
                </a>
                <a
                  href="#testimonials"
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 text-sm font-medium transition-colors"
                >
                  Reviews
                </a>
                <Link
                  href="/privacy"
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 text-sm font-medium transition-colors"
                >
                  Privacy
                </Link>
                <Link
                  href="/data-deletion"
                  className="text-gray-700 hover:text-blue-600 px-3 py-2 text-sm font-medium transition-colors"
                >
                  Data Deletion
                </Link>
                <a
                  href="#download"
                  className="bg-blue-600 text-white px-4 py-2 rounded-full text-sm font-medium hover:bg-blue-700 transition-colors"
                >
                  Download
                </a>
              </div>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center">
            <h2 className="text-5xl sm:text-6xl font-bold text-gray-900 mb-6">
              Understand Your Cough with{' '}
              <span className="gradient-text">AI Technology</span>
            </h2>
            <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
              Get instant, AI-powered analysis of your cough. Educational health insights at
              your fingertips. Remember: This app provides educational information only -
              always consult healthcare professionals for medical advice.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center mb-12">
              <a
                href="#download"
                className="inline-flex items-center justify-center px-8 py-4 text-lg font-medium text-white bg-blue-600 rounded-full hover:bg-blue-700 transition-all transform hover:scale-105"
              >
                <AppleIcon className="w-6 h-6 mr-2" />
                Download on App Store
              </a>
              <Link
                href="/privacy"
                className="inline-flex items-center justify-center px-8 py-4 text-lg font-medium text-blue-600 bg-white border-2 border-blue-600 rounded-full hover:bg-blue-50 transition-all"
              >
                View Privacy Policy
              </Link>
            </div>
          </div>

          {/* Phone Mockup */}
          <div className="relative max-w-md mx-auto">
            <div className="relative">
              <div className="animated-gradient rounded-3xl p-1">
                <div className="bg-gray-900 rounded-3xl p-8 shadow-2xl">
                  <div className="bg-gray-100 rounded-2xl h-96 flex items-center justify-center">
                    <div className="text-center p-8">
                      <div className="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          strokeWidth="1.5"
                          stroke="currentColor"
                          aria-hidden="true"
                          data-slot="icon"
                          className="w-12 h-12 text-blue-600"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            d="M12 18.75a6 6 0 0 0 6-6v-1.5m-6 7.5a6 6 0 0 1-6-6v-1.5m6 7.5v3.75m-3.75 0h7.5M12 15.75a3 3 0 0 1-3-3V4.5a3 3 0 1 1 6 0v8.25a3 3 0 0 1-3 3Z"
                          />
                        </svg>
                      </div>
                      <p className="text-gray-600 font-medium">Tap to analyze your cough</p>
                      <p className="text-sm text-gray-500 mt-2">10-second recording</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">
              Powerful Features for Your Health
            </h2>
            <p className="text-xl text-gray-600">
              Advanced technology meets user-friendly design
            </p>
          </div>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="space-y-4">
              {/* Feature 1 - Active */}
              <div className="p-6 rounded-2xl cursor-pointer transition-all bg-white shadow-xl border-2 border-blue-500">
                <div className="flex items-start">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    aria-hidden="true"
                    data-slot="icon"
                    className="w-8 h-8 text-blue-600 mr-4 flex-shrink-0"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 0 1 3 19.875v-6.75ZM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V8.625ZM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V4.125Z"
                    />
                  </svg>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 mb-2">
                      AI-Powered Analysis
                    </h3>
                    <p className="text-gray-600">
                      Advanced artificial intelligence analyzes your cough patterns to provide
                      instant educational insights.
                    </p>
                  </div>
                </div>
              </div>

              {/* Feature 2 */}
              <div className="p-6 rounded-2xl cursor-pointer transition-all bg-white/50 hover:bg-white hover:shadow-lg">
                <div className="flex items-start">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    aria-hidden="true"
                    data-slot="icon"
                    className="w-8 h-8 text-blue-600 mr-4 flex-shrink-0"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285Z"
                    />
                  </svg>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 mb-2">Privacy First</h3>
                    <p className="text-gray-600">
                      Your health data stays private with anonymous authentication and secure
                      cloud processing.
                    </p>
                  </div>
                </div>
              </div>

              {/* Feature 3 */}
              <div className="p-6 rounded-2xl cursor-pointer transition-all bg-white/50 hover:bg-white hover:shadow-lg">
                <div className="flex items-start">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    aria-hidden="true"
                    data-slot="icon"
                    className="w-8 h-8 text-blue-600 mr-4 flex-shrink-0"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M12 6v6h4.5m4.5 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
                    />
                  </svg>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 mb-2">Instant Results</h3>
                    <p className="text-gray-600">
                      Get comprehensive analysis results in seconds, right on your device.
                    </p>
                  </div>
                </div>
              </div>

              {/* Feature 4 */}
              <div className="p-6 rounded-2xl cursor-pointer transition-all bg-white/50 hover:bg-white hover:shadow-lg">
                <div className="flex items-start">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    aria-hidden="true"
                    data-slot="icon"
                    className="w-8 h-8 text-blue-600 mr-4 flex-shrink-0"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z"
                    />
                  </svg>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 mb-2">
                      Educational Resource
                    </h3>
                    <p className="text-gray-600">
                      Learn about potential causes and management approaches from trusted
                      medical sources.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Feature Detail Panel */}
            <div className="glass-effect rounded-2xl p-8">
              <h3 className="text-2xl font-bold text-gray-900 mb-6">AI-Powered Analysis</h3>
              <ul className="space-y-4">
                <li className="flex items-center">
                  <CheckIcon />
                  <span className="text-gray-700">Cough type identification</span>
                </li>
                <li className="flex items-center">
                  <CheckIcon />
                  <span className="text-gray-700">Severity assessment</span>
                </li>
                <li className="flex items-center">
                  <CheckIcon />
                  <span className="text-gray-700">Pattern analysis</span>
                </li>
                <li className="flex items-center">
                  <CheckIcon />
                  <span className="text-gray-700">Confidence scoring</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section
        id="how-it-works"
        className="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-b from-transparent to-blue-50"
      >
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">How It Works</h2>
            <p className="text-xl text-gray-600">Simple, fast, and educational</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-blue-600">1</span>
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">Record Your Cough</h3>
              <p className="text-gray-600">
                Simply tap the record button and cough naturally for up to 10 seconds
              </p>
            </div>
            <div className="text-center">
              <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-blue-600">2</span>
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">AI Analysis</h3>
              <p className="text-gray-600">
                Our advanced AI processes your cough audio to identify patterns and
                characteristics
              </p>
            </div>
            <div className="text-center">
              <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-2xl font-bold text-blue-600">3</span>
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">Get Insights</h3>
              <p className="text-gray-600">
                Receive detailed educational information about your cough type and potential
                causes
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section id="testimonials" className="py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">What Users Say</h2>
            <p className="text-xl text-gray-600">Join thousands of satisfied users</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white rounded-2xl p-8 shadow-lg">
              <div className="flex mb-4">
                <StarIcon />
                <StarIcon />
                <StarIcon />
                <StarIcon />
                <StarIcon />
              </div>
              <p className="text-gray-700 mb-4">
                This app helped me understand my persistent cough. The analysis was detailed
                and educational.
              </p>
              <p className="font-medium text-gray-900">Sarah M.</p>
            </div>
            <div className="bg-white rounded-2xl p-8 shadow-lg">
              <div className="flex mb-4">
                <StarIcon />
                <StarIcon />
                <StarIcon />
                <StarIcon />
                <StarIcon />
              </div>
              <p className="text-gray-700 mb-4">
                Love the privacy features. Quick, easy to use, and provides helpful insights.
              </p>
              <p className="font-medium text-gray-900">John D.</p>
            </div>
            <div className="bg-white rounded-2xl p-8 shadow-lg">
              <div className="flex mb-4">
                <StarIcon />
                <StarIcon />
                <StarIcon />
                <StarIcon />
                <StarIcon />
              </div>
              <p className="text-gray-700 mb-4">
                The AI analysis is impressive. It correctly identified my cough type and
                suggested I see a doctor.
              </p>
              <p className="font-medium text-gray-900">Emily R.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Download Section */}
      <section
        id="download"
        className="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-b from-transparent to-blue-100"
      >
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-4xl font-bold text-gray-900 mb-4">
            Ready to Understand Your Cough?
          </h2>
          <p className="text-xl text-gray-600 mb-8">
            Download Cough Checker today and get instant AI-powered insights
          </p>
          <div className="bg-white rounded-3xl p-8 shadow-xl mb-8">
            <div className="flex flex-col sm:flex-row items-center justify-center gap-6">
              <div className="text-left">
                <h3 className="text-2xl font-bold text-gray-900 mb-2">Available on iOS</h3>
                <p className="text-gray-600 mb-4">Compatible with iPhone and iPad</p>
                <ul className="text-sm text-gray-600 space-y-1">
                  <li>• Requires iOS 14.0 or later</li>
                  <li>• Free to download and use</li>
                  <li>• No ads or hidden fees</li>
                </ul>
              </div>
              <a
                href="#"
                className="flex-shrink-0 inline-flex items-center justify-center px-8 py-4 text-lg font-medium text-white bg-black rounded-xl hover:bg-gray-900 transition-all transform hover:scale-105"
              >
                <AppleIcon className="w-8 h-8 mr-3" />
                <div className="text-left">
                  <div className="text-xs">Download on the</div>
                  <div className="text-xl font-semibold">App Store</div>
                </div>
              </a>
            </div>
          </div>
          <p className="text-sm text-gray-600">
            Remember: This app provides educational information only. Always consult
            healthcare professionals for medical advice.
          </p>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <h3 className="text-2xl font-bold mb-4">Cough Checker</h3>
              <p className="text-gray-400">
                AI-powered cough analysis for educational health insights
              </p>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4">Quick Links</h4>
              <ul className="space-y-2">
                <li>
                  <a
                    href="#features"
                    className="text-gray-400 hover:text-white transition-colors"
                  >
                    Features
                  </a>
                </li>
                <li>
                  <a
                    href="#how-it-works"
                    className="text-gray-400 hover:text-white transition-colors"
                  >
                    How It Works
                  </a>
                </li>
                <li>
                  <a
                    href="#testimonials"
                    className="text-gray-400 hover:text-white transition-colors"
                  >
                    Reviews
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4">Legal</h4>
              <ul className="space-y-2">
                <li>
                  <Link
                    href="/privacy"
                    className="text-gray-400 hover:text-white transition-colors"
                  >
                    Privacy Policy
                  </Link>
                </li>
                <li>
                  <Link
                    href="/terms"
                    className="text-gray-400 hover:text-white transition-colors"
                  >
                    Terms of Service
                  </Link>
                </li>
                <li>
                  <Link
                    href="/data-deletion"
                    className="text-gray-400 hover:text-white transition-colors"
                  >
                    Data Deletion
                  </Link>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4">Contact</h4>
              <p className="text-gray-400">reid@twintipsolutions.com</p>
              <p className="text-gray-400 mt-2">&copy; 2025 TwinTip Solutions</p>
            </div>
          </div>
          <div className="mt-8 pt-8 border-t border-gray-800 text-center text-sm text-gray-400">
            <p>
              Disclaimer: Cough Checker provides educational information only and is not a
              substitute for professional medical advice, diagnosis, or treatment.
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}
