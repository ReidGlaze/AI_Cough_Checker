import Head from 'next/head'
import '../styles/globals.css'

export default function App({ Component, pageProps }) {
  return (
    <>
      <Head>
        <link rel="icon" href="/favicon.ico" />
        <meta name="theme-color" content="#1e40af" />
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
      <Component {...pageProps} />
    </>
  )
}
