import time
import os
import subprocess
from pathlib import Path

WORKSPACE_DIR = Path(__file__).resolve().parent.parent / "workspace"

def _safe_path(rel_path: str) -> Path:
    target = (WORKSPACE_DIR / rel_path).resolve()
    if not str(target).startswith(str(WORKSPACE_DIR)):
        raise ValueError("Access denied: Path escapes workspace.")
    return target

def list_files() -> str:
    time.sleep(5)
    """Lists all files in the Spring Boot workspace."""
    files = []
    for root, dirs, filenames in os.walk(WORKSPACE_DIR):
        dirs[:] = [d for d in dirs if d not in ("target", ".git")]
        for f in filenames:
            rel = os.path.relpath(os.path.join(root, f), WORKSPACE_DIR)
            files.append(rel)
    return "\n".join(sorted(files)) if files else "Workspace is empty."

def read_file(rel_path: str) -> str:
    time.sleep(5)
    """Reads the content of a file in the workspace."""
    path = _safe_path(rel_path)
    if not path.exists():
        return f"Error: File '{rel_path}' does not exist."
    return path.read_text(encoding="utf-8")

def write_file(rel_path: str, content: str) -> str:
    time.sleep(5)
    """Creates or overwrites a file in the workspace."""
    path = _safe_path(rel_path)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")
    return f"Successfully wrote {rel_path}"

def run_maven_tests() -> str:
    time.sleep(5)
    """Runs 'mvn test -B' in the workspace and returns build/test output."""
    result = subprocess.run(
        ["mvn", "test", "-B"],
        cwd=WORKSPACE_DIR,
        capture_output=True,
        text=True,
        timeout=120
    )
    output = result.stdout + "\n" + result.stderr
    # Return the last 3000 characters to capture compilation errors or JUnit failures clearly
    status = "SUCCESS" if result.returncode == 0 else "FAILURE"
    return f"TEST STATUS: {status}\n\n{output[-3000:]}"
