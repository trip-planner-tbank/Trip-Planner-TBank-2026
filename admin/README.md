# Trip Planner Admin

React Admin application for internal Trip Planner administration.

## Local Development

```bash
npm install
npm run dev
```

The frontend reads the backend URL from `VITE_API_URL`.
Create `admin/.env` from `admin/.env.example` for local overrides.

## Docker

The root `docker-compose.yml` builds this app and serves the production bundle with nginx on `http://localhost:3000`.
