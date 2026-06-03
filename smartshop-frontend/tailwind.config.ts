/**
 * tailwind.config.ts — Tailwind CSS design system configuration
 *
 * PURPOSE:
 * Tailwind is a utility-first CSS framework. This file defines SmartShop design tokens (colors, fonts,
 * animations) that the team reuses across components instead of scattering magic hex values.
 */

import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f5f3ff',
          100: '#ede9fe',
          200: '#ddd6fe',
          300: '#c4b5fd',
          400: '#a78bfa',
          500: '#8b5cf6',
          600: '#7c3aed',
          700: '#6d28d9',
          800: '#5b21b6',
          900: '#4c1d95',
          950: '#2e1065',
        },
        accent: {
          DEFAULT: '#22d3ee',
          50: '#ecfeff',
          100: '#cffafe',
          200: '#a5f3fc',
          300: '#67e8f9',
          400: '#22d3ee',
          500: '#06b6d4',
          600: '#0891b2',
          700: '#0e7490',
          800: '#155e75',
          900: '#164e63',
        },
        surface: {
          DEFAULT: '#12151f',
          elevated: '#1a1f2e',
          hover: '#232a3d',
          muted: '#0c0e14',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        glow: '0 0 24px -4px rgba(139, 92, 246, 0.45)',
        'glow-sm': '0 0 12px -2px rgba(139, 92, 246, 0.35)',
        'glow-accent': '0 0 20px -4px rgba(34, 211, 238, 0.4)',
        card: '0 4px 24px -4px rgba(0, 0, 0, 0.5)',
        'card-hover': '0 12px 40px -8px rgba(139, 92, 246, 0.25), 0 4px 16px -4px rgba(0, 0, 0, 0.4)',
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(ellipse at top, var(--tw-gradient-stops))',
        'mesh-hero':
          'radial-gradient(ellipse 80% 60% at 50% -20%, rgba(139, 92, 246, 0.35), transparent), radial-gradient(ellipse 60% 50% at 100% 0%, rgba(34, 211, 238, 0.15), transparent), radial-gradient(ellipse 50% 40% at 0% 100%, rgba(124, 58, 237, 0.12), transparent)',
        'gradient-accent': 'linear-gradient(135deg, #8b5cf6 0%, #22d3ee 100%)',
        'gradient-accent-hover': 'linear-gradient(135deg, #7c3aed 0%, #06b6d4 100%)',
      },
      animation: {
        'fade-in': 'fadeIn 0.35s ease-out',
        'slide-up': 'slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-down': 'slideDown 0.35s cubic-bezier(0.16, 1, 0.3, 1)',
        'bounce-once': 'bounceOnce 0.45s ease',
        'pulse-glow': 'pulseGlow 2.5s ease-in-out infinite',
        shimmer: 'shimmer 2s linear infinite',
        float: 'float 6s ease-in-out infinite',
        'scale-in': 'scaleIn 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
      },
      keyframes: {
        fadeIn: { from: { opacity: '0' }, to: { opacity: '1' } },
        slideUp: {
          from: { transform: 'translateY(20px)', opacity: '0' },
          to: { transform: 'translateY(0)', opacity: '1' },
        },
        slideDown: {
          from: { transform: 'translateY(-12px)', opacity: '0' },
          to: { transform: 'translateY(0)', opacity: '1' },
        },
        bounceOnce: {
          '0%, 100%': { transform: 'scale(1)' },
          '50%': { transform: 'scale(1.12)' },
        },
        pulseGlow: {
          '0%, 100%': { boxShadow: '0 0 20px -4px rgba(139, 92, 246, 0.3)' },
          '50%': { boxShadow: '0 0 28px -2px rgba(139, 92, 246, 0.55)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        scaleIn: {
          from: { transform: 'scale(0.95)', opacity: '0' },
          to: { transform: 'scale(1)', opacity: '1' },
        },
      },
      transitionTimingFunction: {
        spring: 'cubic-bezier(0.16, 1, 0.3, 1)',
      },
    },
  },
  plugins: [],
};

export default config;
