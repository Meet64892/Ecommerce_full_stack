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

export function Sidebar() {
  const open = useUiStore((s) => s.sidebarOpen);
  const setOpen = useUiStore((s) => s.setSidebarOpen);
  const isDesktop = useMediaQuery('(min-width: 1024px)');

  if (isDesktop) return null;

  return (
    <Transition show={open} as={Fragment}>
      <Dialog onClose={() => setOpen(false)} className="relative z-50 lg:hidden">
        <TransitionChild
          enter="ease-out duration-200"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-150"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black/30" />
        </TransitionChild>
        <TransitionChild
          enter="transition ease-in-out duration-300 transform"
          enterFrom="-translate-x-full"
          enterTo="translate-x-0"
          leave="transition ease-in-out duration-300 transform"
          leaveFrom="translate-x-0"
          leaveTo="-translate-x-full"
        >
          <DialogPanel className="fixed inset-y-0 left-0 w-72 bg-white p-6 shadow-xl">
            <div className="mb-6 flex justify-end">
              <button type="button" onClick={() => setOpen(false)} aria-label="Close sidebar">
                <X className="h-6 w-6" />
              </button>
            </div>
            <nav className="flex flex-col gap-3">
              <Link to={ROUTES.HOME} onClick={() => setOpen(false)} className="text-lg font-medium">
                Home
              </Link>
              <Link to={ROUTES.PRODUCTS} onClick={() => setOpen(false)} className="text-lg font-medium">
                Shop
              </Link>
              <Link to={ROUTES.CART} onClick={() => setOpen(false)} className="text-lg font-medium">
                Cart
              </Link>
            </nav>
          </DialogPanel>
        </TransitionChild>
      </Dialog>
    </Transition>
  );
}
