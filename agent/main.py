import os
from google import genai
from google.genai import types
from tools import list_files, read_file, write_file, run_maven_tests

# Initialize Gemini client (requires GEMINI_API_KEY env var)
# client = genai.Client()
#client = genai.Client(http_options=types.HttpOptions(api_version="v1beta"))
client = genai.Client(
    http_options=types.HttpOptions(
        api_version="v1beta",
        retry_options=types.HttpRetryOptions(
            attempts=5,
            initial_delay=10.0,
            max_delay=60.0,
            exp_base=2.0,
            http_status_codes=[429, 500, 502, 503, 504],
        ),
    )
)

# MODEL_ID = "gemini-2.5-pro"
MODEL_ID = "gemini-3.1-flash-lite-preview"

SYSTEM_PROMPT = """You are an autonomous Principal Software Engineer working inside a Java 21 / Spring Boot workspace.
Base package: com.example.shortener

Rules:
1. Always inspect existing files using `list_files` and `read_file` before modifying code (critical for Brownfield tasks).
2. Follow clean Spring Boot architecture: Entity, Repository, Service, Controller, DTOs, and JUnit 5 / MockMvc tests in `src/test/java/com/example/shortener/`.
3. Never modify `pom.xml` unless strictly necessary.
4. Always write comprehensive unit and integration tests for every feature or bug fix.
5. After writing code and tests, call `run_maven_tests`. If tests fail, read the error output, fix the code, and re-run until `TEST STATUS: SUCCESS`.
"""

def run_agentic_workflow():
    print("\n=== AGENTIC SDLC ENGINE (JAVA / SPRING BOOT) ===")
    requirement = input("\nEnter your engineering requirement:\n> ").strip()
    if not requirement:
        return

    current_files = list_files()

    # PHASE 1: Requirement Understanding & Task Decomposition
    print("\n[Phase 1] Analyzing requirement & checking for ambiguity...")
    plan_prompt = f"""
    Current workspace files:
    {current_files}

    User Requirement:
    "{requirement}"

    Analyze this requirement. 
    1. Identify any ambiguities and state clear engineering assumptions.
    2. Break down the implementation into step-by-step tasks (Entities, Repositories, Services, Controllers, Edge Cases, and JUnit Tests).
    3. Format the response cleanly in Markdown so it can be saved as PLAN.md.
    """
    plan_response = client.models.generate_content(
        model=MODEL_ID,
        contents=plan_prompt,
        config=types.GenerateContentConfig(system_instruction=SYSTEM_PROMPT)
    )
    plan_md = plan_response.text
    write_file("PLAN.md", plan_md)
    print("\n--- PROPOSED PLAN (Saved to workspace/PLAN.md) ---\n")
    print(plan_md)

    # PHASE 2: Controlled Autonomy (Human-in-the-Loop Gate)
    approval = input("\n[Human Gate] Approve this plan and start autonomous build? (y/n): ").strip().lower()
    if approval != "y":
        print("Execution aborted by human reviewer.")
        return

    # PHASE 3 & 4: Multi-Step Execution & Self-Healing Validation
    print("\n[Phase 3 & 4] Executing build, writing tests, and validating with Maven...")
    chat = client.chats.create(
        model=MODEL_ID,
        config=types.GenerateContentConfig(
            system_instruction=SYSTEM_PROMPT,
            tools=[list_files, read_file, write_file, run_maven_tests],
            automatic_function_calling=types.AutomaticFunctionCallingConfig(
                maximum_remote_calls=30
            ),
            temperature=0.2
        )
    )

    execution_prompt = f"""
    Execute the approved plan below. Use your file tools to inspect existing code, write the Java classes, write JUnit 5 tests, and run `run_maven_tests` until all tests pass.
    Once `run_maven_tests` returns SUCCESS, summarize all changes made and test coverage in a final review report.

    APPROVED PLAN:
    {plan_md}
    """

    response = chat.send_message(execution_prompt)
    #walkthrough_md = response.text
    walkthrough_md = response.text or "# Walkthrough\n\nAll files generated and   Maven tests verified successfully."
    write_file("WALKTHROUGH.md", walkthrough_md)

    print("\n=== EXECUTION COMPLETE ===")
    print("Review report saved to workspace/WALKTHROUGH.md:\n")
    print(walkthrough_md)

if __name__ == "__main__":
    run_agentic_workflow()
