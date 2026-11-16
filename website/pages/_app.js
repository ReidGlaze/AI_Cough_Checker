import Head from 'next/head'
import { Montserrat, IBM_Plex_Sans, IBM_Plex_Mono } from 'next/font/google'
import '../styles/globals.css'

// Montserrat matches the captions in the store screenshots; Plex carries the reading text.
const display = Montserrat({
  subsets: ['latin'],
  weight: ['600', '700', '800'],
  variable: '--font-display',
  display: 'swap',
})
const body = IBM_Plex_Sans({
  subsets: ['latin'],
  weight: ['400', '500', '600'],
  variable: '--font-body',
  display: 'swap',
})
const mono = IBM_Plex_Mono({
  subsets: ['latin'],
  weight: ['500'],
  variable: '--font-mono',
  display: 'swap',
})

export default function App({ Component, pageProps }) {
  return (
    <>
      <Head>
        <link rel="icon" href="/favicon.ico" />
        <meta name="theme-color" content="#00838F" />
        <meta
          name="description"
          content="AI-powered cough analysis app that provides instant health insights. Analyze your cough patterns and get educational information about potential causes."
        />
        <meta property="og:title" content="Cough Checker - AI Cough Analysis App" />
        <meta
          property="og:description"
          content="Get instant AI-powered analysis of your cough. Educational health insights at your fingertips."
        />
        <meta property="og:type" content="website" />
        <meta property="og:image" content="/og-image.png" />
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Cough Checker - AI Cough Analysis App" />
        <meta
          name="twitter:description"
          content="Get instant AI-powered analysis of your cough. Educational health insights at your fingertips."
        />
        <meta name="twitter:image" content="/og-image.png" />
      </Head>
      <div className={`${display.variable} ${body.variable} ${mono.variable} font-sans`}>
        <Component {...pageProps} />
      </div>
    </>
  )
}
