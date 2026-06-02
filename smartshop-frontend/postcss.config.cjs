/**
 * postcss.config.cjs — PostCSS pipeline for Tailwind
 *
 * PURPOSE:
 * PostCSS processes CSS after you write it. Tailwind and Autoprefixer run here so Vite can import globals.css.
 *
 * CONNECTED TO:
 * - tailwind.config.ts
 * - src/styles/globals.css
 */

module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
};
