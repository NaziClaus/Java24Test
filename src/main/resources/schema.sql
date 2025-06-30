CREATE TABLE file_records (
  id SERIAL PRIMARY KEY,
  filename TEXT NOT NULL,
  remote_path TEXT UNIQUE NOT NULL,
  size BIGINT NOT NULL,
  remote_mtime TIMESTAMP WITH TIME ZONE NOT NULL,
  downloaded BOOLEAN NOT NULL DEFAULT FALSE,
  last_downloaded TIMESTAMP WITH TIME ZONE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_mtime ON file_records(remote_mtime);
