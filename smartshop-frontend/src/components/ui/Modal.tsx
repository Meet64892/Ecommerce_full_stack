/**
 * Modal.tsx — Accessible dialog with Headless UI + Framer Motion + portal
 *
 * PURPOSE:
 * Demonstrates focus trap, escape/backdrop close, and exit animations.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - @headlessui/react Dialog: ARIA roles, focus management (WCAG 2.4.3).
 * - createPortal: Renders into document.body to escape z-index stacking contexts.
 * - AnimatePresence: Enables exit animations when open becomes false.
 *
 * CONNECTED TO:
 * - Checkout confirmations, quick views
 */

import { Fragment, type ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { Dialog, DialogPanel, DialogTitle, Transition, TransitionChild } from '@headlessui/react';
import { AnimatePresence, motion } from 'framer-motion';
import { X } from 'lucide-react';
import { Button } from './Button';

export interface ModalProps {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
}

/**
 * Modal — Portal-rendered accessible overlay
 *
 * @param props.open - Controls visibility
 * @param props.onClose - Called on Escape, backdrop click, or close button
 */
export function Modal({ open, onClose, title, children }: ModalProps) {
  if (typeof document === 'undefined') return null;

  return createPortal(
    <AnimatePresence>
      {open && (
        <Dialog static open={open} onClose={onClose} className="relative z-50">
          <Transition show={open} as={Fragment}>
            <TransitionChild
              enter="ease-out duration-200"
              enterFrom="opacity-0"
              enterTo="opacity-100"
              leave="ease-in duration-150"
              leaveFrom="opacity-100"
              leaveTo="opacity-0"
            >
              <div className="fixed inset-0 bg-black/40" aria-hidden="true" />
            </TransitionChild>

            <div className="fixed inset-0 flex items-center justify-center p-4">
              <TransitionChild
                as={Fragment}
                enter="ease-out duration-200"
                enterFrom="opacity-0 scale-95"
                enterTo="opacity-100 scale-100"
                leave="ease-in duration-150"
                leaveFrom="opacity-100 scale-100"
                leaveTo="opacity-0 scale-95"
              >
                <DialogPanel
                  as={motion.div}
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -16 }}
                  className="w-full max-w-lg rounded-xl bg-white p-6 shadow-xl"
                >
                  <div className="mb-4 flex items-start justify-between gap-4">
                    {title && (
                      <DialogTitle className="text-lg font-semibold text-gray-900">
                        {title}
                      </DialogTitle>
                    )}
                    <Button variant="ghost" size="sm" onClick={onClose} aria-label="Close">
                      <X className="h-5 w-5" />
                    </Button>
                  </div>
                  {children}
                </DialogPanel>
              </TransitionChild>
            </div>
          </Transition>
        </Dialog>
      )}
    </AnimatePresence>,
    document.body,
  );
}
