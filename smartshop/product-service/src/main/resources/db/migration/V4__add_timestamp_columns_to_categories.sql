-- Add timestamp columns to categories table
ALTER TABLE public.categories
ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW();

ALTER TABLE public.categories
ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- Create index on created_at for better query performance
CREATE INDEX idx_categories_created_at ON public.categories(created_at);

-- Update existing rows to have proper timestamps
UPDATE public.categories SET updated_at = NOW() WHERE updated_at IS NULL;