/**
 * Button.test.tsx — Accessibility-first component tests
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Button } from './Button';

describe('Button', () => {
  it('renders label and handles click', async () => {
    const user = userEvent.setup();
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Add to cart</Button>);
    await user.click(screen.getByRole('button', { name: /add to cart/i }));
    expect(onClick).toHaveBeenCalledOnce();
  });

  it('is disabled when isLoading', () => {
    render(<Button isLoading>Save</Button>);
    expect(screen.getByRole('button', { name: /save/i })).toBeDisabled();
  });
});
