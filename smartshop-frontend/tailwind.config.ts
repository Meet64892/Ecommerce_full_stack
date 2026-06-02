/**
 * tailwind.config.ts — Tailwind CSS design system configuration
 *
 * PURPOSE:
 * Tailwind is a utility-first CSS framework. This file defines SmartShop design tokens (colors, fonts,
 * animations) that the team reuses across components instead of scattering magic hex values.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Utility-first CSS: Compose UI with class names like `bg-primary-500` instead of writing custom CSS per component.
 * - content (purge) paths: Tailwind scans these files and only ships CSS for classes you actually use (JIT).
 * - Design tokens: Named scales (primary-50…900) keep branding consistent.
 * - extend vs replace: `extend` adds to Tailwind defaults; replacing `theme` would wipe built-in colors.
 *
 * CONNECTED TO:
 * - src/styles/globals.css (@tailwind directives)
 * - All components using Tailwind class names
 */

import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          200: '#bfdbfe',
          300: '#93c5fd',
          400: '#60a5fa',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          800: '#1e40af',
          900: '#1e3a8a',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      animation: {
        'fade-in': 'fadeIn 0.2s ease-in',
        'slide-up': 'slideUp 0.3s ease-out',
        'bounce-once': 'bounceOnce 0.4s ease',
      },
      keyframes: {
        fadeIn: { from: { opacity: '0' }, to: { opacity: '1' } },
        slideUp: {
          from: { transform: 'translateY(16px)', opacity: '0' },
          to: { transform: 'translateY(0)', opacity: '1' },
        },
        bounceOnce: {
          '0%, 100%': { transform: 'scale(1)' },
          '50%': { transform: 'scale(1.15)' },
        },
      },
    },
  },
  plugins: [],
};

export default config;
