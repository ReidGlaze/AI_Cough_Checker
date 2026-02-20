import Head from 'next/head'
import Link from 'next/link'

export default function DataDeletion() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <Head>
        <title>Data Deletion - Cough Checker</title>
        <meta name="description" content="Learn how to delete your data from Cough Checker" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Link */}
        <Link
          href="/"
          className="inline-flex items-center text-indigo-600 hover:text-indigo-700 mb-8"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth="1.5"
            stroke="currentColor"
            aria-hidden="true"
            data-slot="icon"
            className="h-5 w-5 mr-2"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M15.75 19.5 8.25 12l7.5-7.5"
            />
          </svg>
          Back to Home
        </Link>

        {/* Header Card */}
        <div className="bg-white rounded-2xl shadow-xl p-8 mb-8">
          <div className="flex items-center mb-6">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth="1.5"
              stroke="currentColor"
              aria-hidden="true"
              data-slot="icon"
              className="h-10 w-10 text-red-500 mr-4"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
              />
            </svg>
            <h1 className="text-4xl font-bold text-gray-900">Data Deletion</h1>
          </div>
          <p className="text-lg text-gray-600">
            We respect your privacy and provide you with full control over your data.
            Here&apos;s how to delete your account and associated data.
          </p>
        </div>

        {/* How to Delete Card */}
        <div className="bg-white rounded-2xl shadow-xl p-8 mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">How to Delete Your Data</h2>
          <div className="space-y-6">
            {/* Step 1 */}
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <div className="flex items-center justify-center h-10 w-10 rounded-full bg-indigo-100 text-indigo-600 font-bold">
                  1
                </div>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Open the Cough Checker App
                </h3>
                <p className="text-gray-600">
                  Launch the Cough Checker app on your mobile device.
                </p>
              </div>
            </div>

            {/* Step 2 */}
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <div className="flex items-center justify-center h-10 w-10 rounded-full bg-indigo-100 text-indigo-600 font-bold">
                  2
                </div>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Navigate to History &amp; Info
                </h3>
                <p className="text-gray-600">
                  From the Home Screen, click on the &quot;History &amp; Info&quot; button.
                </p>
                <div className="mt-3 p-4 bg-gray-50 rounded-lg">
                  <div className="flex items-center text-sm text-gray-700">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth="1.5"
                      stroke="currentColor"
                      aria-hidden="true"
                      data-slot="icon"
                      className="h-5 w-5 mr-2 text-gray-500"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="m2.25 12 8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25"
                      />
                    </svg>
                    <span>Home Screen</span>
                    <span className="mx-2">&rarr;</span>
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth="1.5"
                      stroke="currentColor"
                      aria-hidden="true"
                      data-slot="icon"
                      className="h-5 w-5 mr-2 text-gray-500"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 0 0 2.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 0 0-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 0 0 .75-.75 2.25 2.25 0 0 0-.1-.664m-5.8 0A2.251 2.251 0 0 1 13.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25ZM6.75 12h.008v.008H6.75V12Zm0 3h.008v.008H6.75V15Zm0 3h.008v.008H6.75V18Z"
                      />
                    </svg>
                    <span>History &amp; Info</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Step 3 */}
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <div className="flex items-center justify-center h-10 w-10 rounded-full bg-indigo-100 text-indigo-600 font-bold">
                  3
                </div>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Go to the Info Tab
                </h3>
                <p className="text-gray-600">
                  Once in History &amp; Info, navigate to the &quot;Info&quot; tab.
                </p>
              </div>
            </div>

            {/* Step 4 */}
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <div className="flex items-center justify-center h-10 w-10 rounded-full bg-indigo-100 text-indigo-600 font-bold">
                  4
                </div>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  Click Delete Account
                </h3>
                <p className="text-gray-600">
                  In the Info tab, you&apos;ll find a &quot;Delete Account&quot; button. Click
                  on it to initiate the deletion process.
                </p>
                <div className="mt-3 p-4 bg-red-50 rounded-lg border border-red-200">
                  <div className="flex items-center">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth="1.5"
                      stroke="currentColor"
                      aria-hidden="true"
                      data-slot="icon"
                      className="h-5 w-5 mr-2 text-red-600"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"
                      />
                    </svg>
                    <span className="text-red-700 font-medium">Delete Account</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* What Gets Deleted Card */}
        <div className="bg-white rounded-2xl shadow-xl p-8 mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">What Gets Deleted</h2>
          <div className="space-y-4">
            <div className="flex items-start">
              <div className="flex-shrink-0 mt-1">
                <div className="h-5 w-5 rounded-full bg-green-100 flex items-center justify-center">
                  <span className="text-green-600 text-xs">&#10003;</span>
                </div>
              </div>
              <div className="ml-3">
                <h4 className="font-semibold text-gray-900">Your Account Information</h4>
                <p className="text-gray-600">
                  All personal account details and login credentials
                </p>
              </div>
            </div>
            <div className="flex items-start">
              <div className="flex-shrink-0 mt-1">
                <div className="h-5 w-5 rounded-full bg-green-100 flex items-center justify-center">
                  <span className="text-green-600 text-xs">&#10003;</span>
                </div>
              </div>
              <div className="ml-3">
                <h4 className="font-semibold text-gray-900">Cough Analysis History</h4>
                <p className="text-gray-600">
                  All your past cough recordings and analysis results
                </p>
              </div>
            </div>
            <div className="flex items-start">
              <div className="flex-shrink-0 mt-1">
                <div className="h-5 w-5 rounded-full bg-green-100 flex items-center justify-center">
                  <span className="text-green-600 text-xs">&#10003;</span>
                </div>
              </div>
              <div className="ml-3">
                <h4 className="font-semibold text-gray-900">User Preferences</h4>
                <p className="text-gray-600">
                  Settings and preferences associated with your account
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Important Exception */}
        <div className="bg-yellow-50 border-2 border-yellow-200 rounded-2xl p-8 mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
            <span className="text-yellow-600 mr-2">&#9888;&#65039;</span>Important Exception
          </h2>
          <div className="space-y-4">
            <p className="text-gray-700">
              <strong>
                Reports submitted through &quot;Report Harmful Content&quot; will NOT be
                deleted.
              </strong>
            </p>
            <p className="text-gray-600">This exception exists because:</p>
            <ul className="list-disc list-inside text-gray-600 space-y-2 ml-4">
              <li>Harmful content reports need to be reviewed by our team</li>
              <li>
                These reports help us improve the app&apos;s safety and accuracy
              </li>
              <li>
                They may contain important information about potential hallucinations or misuse
              </li>
            </ul>
            <p className="text-gray-600 mt-4">
              These reports are stored separately and are only accessible to our review team
              for quality and safety purposes.
            </p>
          </div>
        </div>

        {/* Deletion Timeline Card */}
        <div className="bg-white rounded-2xl shadow-xl p-8 mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Deletion Timeline</h2>
          <div className="space-y-4">
            <div className="border-l-4 border-indigo-500 pl-4">
              <h4 className="font-semibold text-gray-900">Immediate</h4>
              <p className="text-gray-600">
                Your account is immediately deactivated and inaccessible
              </p>
            </div>
            <div className="border-l-4 border-indigo-500 pl-4">
              <h4 className="font-semibold text-gray-900">Within 24 hours</h4>
              <p className="text-gray-600">
                All your personal data is permanently deleted from our active databases
              </p>
            </div>
            <div className="border-l-4 border-indigo-500 pl-4">
              <h4 className="font-semibold text-gray-900">Within 30 days</h4>
              <p className="text-gray-600">
                Your data is removed from all backup systems
              </p>
            </div>
          </div>
        </div>

        {/* Need Help Card */}
        <div className="bg-gray-50 rounded-2xl p-8 text-center">
          <h3 className="text-xl font-semibold text-gray-900 mb-4">Need Help?</h3>
          <p className="text-gray-600 mb-6">
            If you have any questions about data deletion or encounter any issues, please
            don&apos;t hesitate to contact our support team.
          </p>
          <a
            href="mailto:reid@twintipsolutions.com"
            className="inline-flex items-center px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors"
          >
            Contact Support
          </a>
        </div>
      </div>
    </div>
  )
}
