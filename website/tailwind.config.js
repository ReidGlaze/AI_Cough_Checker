/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx}',
    './components/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        // One accent: the teal used in the store screenshots.
        brand: {
          50: '#EAF4F5',
          100: '#D3E9EB',
          200: '#A6D3D8',
          500: '#0096A3',
          600: '#00838F',
          700: '#006973',
          800: '#0A4B52',
          900: '#0B2E32',
        },
        ink: '#101A1B',
        paper: '#F4F4F2',
        line: '#D9D9D4',
      },
      fontFamily: {
        display: ['var(--font-display)', 'system-ui', 'sans-serif'],
        sans: ['var(--font-body)', 'system-ui', '-apple-system', 'Segoe UI', 'sans-serif'],
        mono: ['var(--font-mono)', 'ui-monospace', 'SFMono-Regular', 'Menlo', 'monospace'],
      },
    },
  },
  plugins: [],
}
