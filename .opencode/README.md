# Reqlint Three-Agent Team

This directory contains the configuration for the three-agent development team that collaborates on reqlint development.

## Team Structure

```
👨‍💻 Three-Agent Team
├── 🏗️ Architect - Design & Planning
├── 💻 Developer - Implementation
└── ✅ Tester/Reviewer - QA & Code Review
```

## How to Use

### Option 1: Sequential Manual Invocation

For a new feature, invoke agents in order:

```bash
# 1. Start with Architect
gp task --agent reqlint-architect "I want to add [feature description]. Please analyze the codebase and design the solution."

# 2. Then Developer (pass the spec)
gp task --agent reqlint-developer "Based on the spec above, implement the feature."

# 3. Finally Tester
gp task --agent reqlint-tester "Review the implementation and verify with tests."
```

### Option 2: This Chat Interface

Simply describe your feature request, and I'll coordinate the team:

> "I want to add JSON output support for the traceability report"

## Team Workflow

```
Feature Request
       ↓
  ┌─────────────────────────────────┐
  │   🏗️ ARCHITECT                 │
  │   - Analyze codebase           │
  │   - Design solution            │
  │   - Create specifications      │
  └─────────────────────────────────┘
       ↓
  ┌─────────────────────────────────┐
  │   💻 DEVELOPER                 │
  │   - Receive specs              │
  │   - Implement code             │
  │   - Write unit tests           │
  │   - Run mvn clean test         │
  └─────────────────────────────────┘
       ↓
  ┌─────────────────────────────────⤵ (feedback loop)
  │   ✅ TESTER                    │
  │   - Code review                │
  │   - Run all tests              │
  │   - Check quality              │
  │   - Sign-off / Issues list     │
  └─────────────────────────────────┘
       ↓
  Final Delivery
```

## Agent Responsibilities

### Architect
- ✅ Analyze requirements
- ✅ Explore existing codebase
- ✅ Design system architecture
- ✅ Create technical specifications
- ❌ Does NOT implement code

### Developer
- ✅ Implement features per specs
- ✅ Write unit tests
- ✅ Ensure code compiles
- ✅ Follow code style
- ✅ Provide implementation updates

### Tester/Reviewer
- ✅ Create test plans
- ✅ Write unit tests
- ✅ Code review
- ✅ Run verification tests
- ✅ Provide QA sign-off

## Files in This Directory

```
.opencode/
├── agents/                    ← Agent configurations
│   ├── reqlint-architect/
│   │   └── opencode.json
│   ├── reqlint-developer/
│   │   └── opencode.json
│   ├── reqlint-tester/
│   │   └── opencode.json
│   └── reqlint-team.json     ← Team preset
└── README.md                  ← This file
```

## Example Feature Request

```
I want to add a new command-line option to the Maven plugin:

  mvn reqlint:check-format

This should:
1. Parse LaTeX specification files
2. Check that requirements follow the ER -> SR -> TC hierarchy
3. Output a formatted traceability report
4. Fail the build if orphan requirements are found
```

## Notes

- The sandbox module is used for testing the plugin and should not be modified
- Always run `mvn clean test` after development
- Use Lombok annotations consistently
- Follow existing naming conventions
