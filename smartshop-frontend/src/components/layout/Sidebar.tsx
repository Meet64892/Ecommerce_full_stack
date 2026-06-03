/**
 * Sidebar.tsx — Mobile filter/navigation drawer
 *
 * CONNECTED TO:
 * - uiStore.sidebarOpen
 */

import { Fragment } from 'react';
import { Link } from 'react-router-dom';
import { Dialog, DialogPanel, Transition, TransitionChild } from '@headlessui/react';
import { X } from 'lucide-react';
import { useUiStore } from '@store/uiStore';
import { ROUTES } from '@utils/constants';
import { useMediaQuery } from '@hooks/useMediaQuery';
import { cn } from '@utils/cn';

const links = [
  { to: ROUTES.HOME, label: 'Home' },
  { to: ROUTES.PRODUCTS, label: 'Shop' },
  { to: ROUTES.CART, label: 'Cart' },
];

export function Sidebar() {
  const open = useUiStore((s) => s.sidebarOpen);
  const setOpen = useUiStore((s) => s.setSidebarOpen);
  const isDesktop = useMediaQuery('(min-width: 1024px)');

  if (isDesktop) return null;

  return (
    <Transition show={open} as={Fragment}>
      <Dialog onClose={() => setOpen(false)} className="relative z-50 lg:hidden">
        <TransitionChild
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm" />
        </TransitionChild>
        <TransitionChild
          enter="transition ease-spring duration-350 transform"
          enterFrom="-translate-x-full"
          enterTo="translate-x-0"
          leave="transition ease-in duration-250 transform"
          leaveFrom="translate-x-0"
          leaveTo="-translate-x-full"
        >
          <DialogPanel className="glass-panel fixed inset-y-0 left-0 w-72 p-6 shadow-card-hover">
            <div className="mb-8 flex justify-end">
              <button
                type="button"
                onClick={() => setOpen(false)}
                className="icon-btn"
                aria-label="Close sidebar"
              >
                <X className="h-6 w-6" />
              </button>
            </div>
            <nav className="flex flex-col gap-2">
              {links.map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  onClick={() => setOpen(false)}
                  className={cn(
                    'rounded-lg px-4 py-3 text-lg font-medium text-slate-300 transition-all duration-200',
                    'hover:bg-primary-600/15 hover:text-white hover:pl-5',
                  )}
                >
                  {link.label}
                </Link>
              ))}
            </nav>
          </DialogPanel>
        </TransitionChild>
      </Dialog>
    </Transition>
  );
}
