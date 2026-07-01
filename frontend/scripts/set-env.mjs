// Generates src/environments/environment.ts from the API_BASE_URL env var.
// Runs automatically before `npm run build` (see the "prebuild" script).
// Locally it defaults to the dev backend; on Vercel set API_BASE_URL to the Render URL.
import { writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const apiBaseUrl = process.env.API_BASE_URL || 'http://localhost:8080';
const target = join(dirname(fileURLToPath(import.meta.url)), '..', 'src', 'environments', 'environment.ts');

const contents = `// Generated from the API_BASE_URL env var by scripts/set-env.mjs — do not edit by hand.
export const environment = {
  apiBaseUrl: '${apiBaseUrl}',
};
`;

writeFileSync(target, contents);
console.log(`[set-env] wrote environment.ts (apiBaseUrl=${apiBaseUrl})`);
