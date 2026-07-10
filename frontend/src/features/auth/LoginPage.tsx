import { useEffect, useState, type FormEvent, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useNotify } from "react-admin";
import {
  Avatar,
  Box,
  Button,
  Container,
  Link,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import PersonAddOutlinedIcon from "@mui/icons-material/PersonAddOutlined";

import { authProvider } from "../../app/providers/authProvider";

export function LoginPage() {
  const navigate = useNavigate();
  const notify = useNotify();
  const [isSignup, setIsSignup] = useState(false);

  useEffect(() => {
    setIsSignup(window.location.hash === "#/signup");
    const handleHashChange = () => setIsSignup(window.location.hash === "#/signup");
    window.addEventListener("hashchange", handleHashChange);
    return () => window.removeEventListener("hashchange", handleHashChange);
  }, []);

  if (isSignup) {
    return <SignupForm onSwitchToLogin={() => setIsSignup(false)} />;
  }

  return <LoginForm onSwitchToSignup={() => setIsSignup(true)} notify={notify} navigate={navigate} />;
}

function LoginForm({
  onSwitchToSignup,
  notify,
  navigate,
}: {
  onSwitchToSignup: () => void;
  notify: (message: string, options: { type: "error" | "success" }) => void;
  navigate: (path: string) => void;
}) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsLoading(true);

    try {
      await authProvider.login({ username, password });
      navigate("/");
    } catch (error) {
      const message =
        error instanceof Error ? error.message : "Login failed. Please try again.";
      notify(message, { type: "error" });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Paper elevation={3} sx={{ mt: 8, p: 4, display: "flex", flexDirection: "column", alignItems: "center" }}>
        <Avatar sx={{ m: 1, bgcolor: "primary.main" }}>
          <LockOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5">
          Sign in
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1, width: "100%" }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            autoFocus
            value={username}
            onChange={(event: ChangeEvent<HTMLInputElement>) => setUsername(event.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
            value={password}
            onChange={(event: ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            disabled={isLoading}
            sx={{ mt: 3, mb: 2 }}
          >
            {isLoading ? "Signing in…" : "Sign In"}
          </Button>
          <Box sx={{ textAlign: "center" }}>
            <Link component="button" type="button" onClick={onSwitchToSignup} variant="body2">
              Don&apos;t have an account? Sign up
            </Link>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
}

function SignupForm({ onSwitchToLogin }: { onSwitchToLogin: () => void }) {
  const notify = useNotify();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsLoading(true);

    try {
      await authProvider.signup({ username, email, password });
      notify("Account created successfully. Please sign in.", { type: "success" });
      onSwitchToLogin();
    } catch (error) {
      const message =
        error instanceof Error ? error.message : "Signup failed. Please try again.";
      notify(message, { type: "error" });
      setIsLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Paper elevation={3} sx={{ mt: 8, p: 4, display: "flex", flexDirection: "column", alignItems: "center" }}>
        <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
          <PersonAddOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5">
          Sign up
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1, width: "100%" }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            autoFocus
            value={username}
            onChange={(event: ChangeEvent<HTMLInputElement>) => setUsername(event.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Email"
            name="email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(event: ChangeEvent<HTMLInputElement>) => setEmail(event.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="new-password"
            value={password}
            onChange={(event: ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            disabled={isLoading}
            sx={{ mt: 3, mb: 2 }}
          >
            {isLoading ? "Creating account…" : "Sign Up"}
          </Button>
          <Box sx={{ textAlign: "center" }}>
            <Link component="button" type="button" onClick={onSwitchToLogin} variant="body2">
              Already have an account? Sign in
            </Link>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
}
