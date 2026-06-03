/**
 * ProductImageGallery.tsx — Image carousel via react-image-gallery
 */

import ImageGallery from 'react-image-gallery';
import 'react-image-gallery/styles/css/image-gallery.css';

export function ProductImageGallery({ productName }: { productName: string }) {
  const images = [
    {
      original: `https://placehold.co/800x600/2563eb/ffffff?text=${encodeURIComponent(productName)}`,
      thumbnail: `https://placehold.co/200x150/2563eb/ffffff?text=1`,
    },
    {
      original: `https://placehold.co/800x600/1d4ed8/ffffff?text=${encodeURIComponent(productName)}+2`,
      thumbnail: `https://placehold.co/200x150/1d4ed8/ffffff?text=2`,
    },
  ];

  return (
    <div className="card-interactive overflow-hidden">
      <ImageGallery items={images} showPlayButton={false} lazyLoad />
    </div>
  );
}
