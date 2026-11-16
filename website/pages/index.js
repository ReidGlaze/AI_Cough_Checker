import Head from 'next/head'
import Link from 'next/link'

const CheckIcon = () => (
  <svg className="w-5 h-5 text-brand-200 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true">
    <path
      fillRule="evenodd"
      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
      clipRule="evenodd"
    />
  </svg>
)

const AppleIcon = ({ className }) => (
  <svg className={className} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
    <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-.48-3.24 0-1.44.62-2.2.44-3.06-.35C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.38 5.98.48 7.13-.57 1.5-1.31 2.99-2.54 4.09l.01-.01zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.51-3.74 4.25z" />
  </svg>
)

const PlayStoreIcon = ({ className }) => (
  <svg className={className} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
    <path d="M3.609 1.814L13.792 12 3.61 22.186a.996.996 0 0 1-.61-.92V2.734a1 1 0 0 1 .609-.92zm10.89 10.893l2.302 2.302-10.937 6.333 8.635-8.635zm3.199-3.199l2.302 2.302a1 1 0 0 1 0 1.38l-2.302 2.302L15.394 12l2.304-2.492zM5.864 2.658L16.8 8.99l-2.302 2.302-8.634-8.634z" />
  </svg>
)

const screenshots = [
  {
    src: '/screenshots/01-record.webp',
    alt: 'Cough Checker record screen: record your cough and get an AI read in seconds',
  },
  {
    src: '/screenshots/02-cough-type.webp',
    alt: 'Analysis result showing whether the cough is dry or wet',
  },
  {
    src: '/screenshots/03-causes.webp',
    alt: 'Possible causes of the cough, plainly explained',
  },
  {
    src: '/screenshots/04-history.webp',
    alt: 'History screen tracking every cough check over time',
  },
  {
    src: '/screenshots/05-privacy.webp',
    alt: 'Information screen with the option to delete your data anytime',
  },
]

const APP_STORE_URL = 'https://apps.apple.com/us/app/ai-cough-checker/id6748887358'
const PLAY_STORE_URL = 'https://play.google.com/store/apps/details?id=com.twintipsolutions.cough'

// Heroicons (outline) paths
const features = [
  {
    title: 'AI-Powered Analysis',
    text: 'Advanced artificial intelligence analyzes your cough patterns to provide instant educational insights.',
    icon: 'M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 0 1 3 19.875v-6.75ZM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V8.625ZM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V4.125Z',
  },
  {
    title: 'Privacy First',
    text: 'You stay anonymous. Your recording is sent securely to the cloud for analysis and is not stored afterward.',
    icon: 'M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285Z',
  },
  {
    title: 'Instant Results',
    text: 'Get comprehensive analysis results back on your phone in seconds.',
    icon: 'M12 6v6h4.5m4.5 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',
  },
  {
    title: 'Educational Resource',
    text: 'Learn about potential causes and management approaches from trusted medical sources.',
    icon: 'M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z',
  },
]

const covers = [
  'Cough type identification',
  'Severity assessment',
  'Pattern analysis',
  'Confidence scoring',
]

const steps = [
  {
    title: 'Record Your Cough',
    text: 'Simply tap the record button and cough naturally for up to 10 seconds',
  },
  {
    title: 'AI Analysis',
    text: 'Our advanced AI processes your cough audio to identify patterns and characteristics',
  },
  {
    title: 'Get Insights',
    text: 'Receive detailed educational information about your cough type and potential causes',
  },
]

// The waveform from the app's result screen, used as the wordmark's mark.
const Waveform = ({ className }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.25" strokeLinecap="round" aria-hidden="true">
    <path d="M3 12v0M6.6 9v6M10.2 5v14M13.8 8v8M17.4 3.5v17M21 10.5v3" />
  </svg>
)

const StoreButton = ({ href, Icon, small, big, tone = 'dark' }) => (
  <a
    href={href}
    target="_blank"
    rel="noopener noreferrer"
    aria-label={`${small} ${big}`}
    className={`inline-flex items-center justify-center rounded-xl px-6 py-3 transition-colors ${
      tone === 'dark'
        ? 'bg-ink text-white hover:bg-brand-800'
        : 'bg-white text-ink hover:bg-brand-50'
    }`}
  >
    <Icon className="w-7 h-7 mr-3" />
    <span className="text-left leading-tight">
      <span className="block text-[11px] font-medium opacity-80">{small}</span>
      <span className="block font-display text-lg font-bold">{big}</span>
    </span>
  </a>
)

const navLink = 'text-ink/70 hover:text-brand-700 px-3 py-2 text-sm font-medium transition-colors'
const footLink = 'text-white/60 hover:text-white transition-colors'

export default function Home() {
  return (
    <div className="min-h-screen bg-paper">
      <Head>
        <title>Cough Checker - AI-Powered Cough Analysis App</title>
      </Head>

      {/* Navigation */}
      <nav className="fixed top-0 w-full z-50 glass-effect">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <a href="#top" className="flex items-center font-display text-lg font-extrabold tracking-tight text-ink">
              <Waveform className="w-6 h-6 mr-2 text-brand-600" />
              Cough Checker
            </a>
            <div className="flex items-center">
              <div className="hidden md:flex items-baseline space-x-4 mr-4">
                <a href="#features" className={navLink}>
                  Features
                </a>
                <a href="#how-it-works" className={navLink}>
                  How It Works
                </a>
                <Link href="/privacy" className={navLink}>
                  Privacy
                </Link>
                <Link href="/data-deletion" className={navLink}>
                  Data Deletion
                </Link>
              </div>
              <a
                href="#download"
                className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-brand-700 transition-colors"
              >
                Download
              </a>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section id="top" className="pt-28 sm:pt-36 pb-12 sm:pb-16 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-10 lg:gap-12 items-end">
          <div className="lg:col-span-8">
            <h1 className="font-display font-extrabold text-ink text-[2.6rem] leading-[1.05] sm:text-6xl lg:text-7xl tracking-tight">
              Understand Your Cough with{' '}
              <span className="text-brand-600 whitespace-nowrap">AI Technology</span>
            </h1>
            <p className="mt-6 text-lg sm:text-xl text-ink/75 max-w-2xl leading-relaxed">
              Get instant, AI-powered analysis of your cough. Educational health insights at
              your fingertips.
            </p>
            <div className="mt-8 flex flex-col sm:flex-row gap-3">
              <StoreButton href={APP_STORE_URL} Icon={AppleIcon} small="Download on" big="App Store" />
              <StoreButton href={PLAY_STORE_URL} Icon={PlayStoreIcon} small="Get it on" big="Google Play" />
            </div>
          </div>
          <div className="lg:col-span-4">
            <dl className="border-t border-ink">
              <div className="flex justify-between gap-4 border-b border-line py-3">
                <dt className="eyebrow">Recording</dt>
                <dd className="text-sm font-medium text-right">10-second recording</dd>
              </div>
              <div className="flex justify-between gap-4 border-b border-line py-3">
                <dt className="eyebrow">Price</dt>
                <dd className="text-sm font-medium text-right">Free, no ads or hidden fees</dd>
              </div>
              <div className="flex justify-between gap-4 border-b border-line py-3">
                <dt className="eyebrow">Platforms</dt>
                <dd className="text-sm font-medium text-right">iOS &amp; Android</dd>
              </div>
            </dl>
            <p className="mt-4 text-sm text-ink/70 leading-relaxed">
              Remember: This app provides educational information only - always consult
              healthcare professionals for medical advice.
            </p>
          </div>
        </div>
      </section>

      {/* Screenshots Section */}
      <section id="screenshots" className="pb-16 sm:pb-24 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <div className="flex flex-wrap items-baseline justify-between gap-x-6 gap-y-1 mb-6 px-4 sm:px-6 lg:px-0">
            <h2 className="font-display text-xl font-bold text-ink">See It in Action</h2>
            <p className="text-sm text-ink/70">From recording to results in a few taps</p>
          </div>
          <ul className="flex gap-4 overflow-x-auto snap-x snap-mandatory px-4 pb-4 sm:px-6 lg:grid lg:grid-cols-5 lg:gap-5 lg:overflow-visible lg:px-0 lg:pb-0">
            {screenshots.map((shot) => (
              <li
                key={shot.src}
                className="snap-center shrink-0 w-[68%] max-w-[280px] sm:w-[40%] lg:w-auto lg:max-w-none"
              >
                <img
                  src={shot.src}
                  alt={shot.alt}
                  width={600}
                  height={1304}
                  className="w-full h-auto rounded-xl border border-line"
                />
              </li>
            ))}
          </ul>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 bg-white border-y border-line">
        <div className="max-w-6xl mx-auto">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 lg:gap-12">
            <div className="lg:col-span-4">
              <p className="eyebrow mb-4">Features</p>
              <h2 className="font-display text-3xl sm:text-4xl font-extrabold text-ink leading-tight">
                Powerful Features for Your Health
              </h2>
              <p className="mt-4 text-lg text-ink/70">
                Advanced technology meets user-friendly design
              </p>
            </div>
            <div className="lg:col-span-8 grid grid-cols-1 sm:grid-cols-2 gap-x-10">
              {features.map((f) => (
                <div key={f.title} className="border-t border-line py-6">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    aria-hidden="true"
                    className="w-7 h-7 text-brand-600 mb-4"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" d={f.icon} />
                  </svg>
                  <h3 className="font-display text-lg font-bold text-ink mb-2">{f.title}</h3>
                  <p className="text-ink/70 leading-relaxed">{f.text}</p>
                </div>
              ))}
            </div>
          </div>

          {/* What each analysis covers */}
          <div className="mt-10 rounded-2xl bg-brand-900 text-white p-8 sm:p-10 lg:flex lg:items-center lg:gap-12">
            <h3 className="font-display text-2xl font-bold lg:w-1/3 mb-6 lg:mb-0">
              What Each Analysis Covers
            </h3>
            <ul className="grid grid-cols-1 sm:grid-cols-2 gap-x-10 gap-y-4 lg:flex-1">
              {covers.map((c) => (
                <li key={c} className="flex items-center">
                  <CheckIcon />
                  <span className="text-white/90">{c}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section id="how-it-works" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <div className="mb-12 max-w-2xl">
            <p className="eyebrow mb-4">Three steps</p>
            <h2 className="font-display text-3xl sm:text-4xl font-extrabold text-ink mb-4">
              How It Works
            </h2>
            <p className="text-lg text-ink/70">Simple, fast, and educational</p>
          </div>
          <ol className="grid grid-cols-1 md:grid-cols-3 gap-x-10 gap-y-8">
            {steps.map((s, i) => (
              <li key={s.title} className="border-t-2 border-ink pt-5">
                <span className="font-mono text-sm font-medium text-brand-700">0{i + 1}</span>
                <h3 className="font-display text-xl font-bold text-ink mt-3 mb-2">{s.title}</h3>
                <p className="text-ink/70 leading-relaxed">{s.text}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      {/* Download Section */}
      <section id="download" className="py-16 sm:py-24 px-4 sm:px-6 lg:px-8 bg-brand-600 text-white">
        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-10 items-center">
          <div className="lg:col-span-7">
            <h2 className="font-display text-3xl sm:text-5xl font-extrabold leading-tight mb-4">
              Ready to Understand Your Cough?
            </h2>
            <p className="text-lg text-white/90">
              Download Cough Checker today and get instant AI-powered insights
            </p>
          </div>
          <div className="lg:col-span-5">
            <h3 className="font-display text-xl font-bold mb-1">Available on iOS &amp; Android</h3>
            <p className="text-white/90 mb-5">Free to download and use — no ads or hidden fees</p>
            <div className="flex flex-col sm:flex-row gap-3">
              <StoreButton href={APP_STORE_URL} Icon={AppleIcon} small="Download on the" big="App Store" tone="light" />
              <StoreButton href={PLAY_STORE_URL} Icon={PlayStoreIcon} small="Get it on" big="Google Play" tone="light" />
            </div>
          </div>
          <p className="lg:col-span-12 text-sm text-white/90 border-t border-white/25 pt-6">
            Remember: This app provides educational information only. Always consult
            healthcare professionals for medical advice.
          </p>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-ink text-white py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <h3 className="flex items-center font-display text-lg font-extrabold mb-4">
                <Waveform className="w-6 h-6 mr-2 text-brand-200" />
                Cough Checker
              </h3>
              <p className="text-white/60">
                AI-powered cough analysis for educational health insights
              </p>
            </div>
            <div>
              <h4 className="eyebrow !text-white/50 mb-4">Quick Links</h4>
              <ul className="space-y-2">
                <li>
                  <a href="#features" className={footLink}>
                    Features
                  </a>
                </li>
                <li>
                  <a href="#how-it-works" className={footLink}>
                    How It Works
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="eyebrow !text-white/50 mb-4">Legal</h4>
              <ul className="space-y-2">
                <li>
                  <Link href="/privacy" className={footLink}>
                    Privacy Policy
                  </Link>
                </li>
                <li>
                  <Link href="/terms" className={footLink}>
                    Terms of Service
                  </Link>
                </li>
                <li>
                  <Link href="/data-deletion" className={footLink}>
                    Data Deletion
                  </Link>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="eyebrow !text-white/50 mb-4">Contact</h4>
              <p>
                <a href="mailto:reid@twintipsolutions.com" className={footLink}>
                  reid@twintipsolutions.com
                </a>
              </p>
              <p className="text-white/60 mt-2">&copy; 2026 TwinTip Solutions</p>
            </div>
          </div>
          <div className="mt-8 pt-8 border-t border-white/15 text-sm text-white/60">
            <p>
              Disclaimer: Cough Checker provides educational information only and is not a
              substitute for professional medical advice, diagnosis, or treatment.
            </p>
            <p className="mt-3">
              Made by{' '}
              <a
                href="https://twintipsolutions.com"
                target="_blank"
                rel="noopener noreferrer"
                className="underline underline-offset-2 hover:text-white transition-colors"
              >
                Twin Tip Solutions
              </a>
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}
