# mc-android-app
this app is build on android and kotlin

## specs:

- this is a mock app that I want to build for an android app.
- it should look like a professional anti-virus app (FakeCo) and attempt to mock services an anti-virus app would do
- the app design should be able to match the above goal. 


Inspired by Harper Reed's blog here:
https://harper.blog/2025/02/16/my-llm-codegen-workflow-atm/

## spec.md creation:

the spec.md was created by making OpenHands (backed by Claude 3.7S) ask me one question at a time. I started with just the 3 specs seed bullets above and it asked me yes/no questions to create the context, and I asked to create a spec. then I enhanced it using a modified version Harpers prompt:

**Now that we’ve wrapped up the initial spec.md, can you update these findings into a comprehensive, developer-ready specification? Include all relevant requirements, architecture choices, data handling details, error handling strategies, and a testing plan so a developer can immediately begin implementation. update this data into the same spec.md and send me a PR**

## prompt_plan.md creation:
-  after the spec.md was enhanced I took that file dropped it into Claude 4 Opus with this prompt (essentially asking Claude to create the codegen prompts itself!)



**Draft a detailed, step-by-step blueprint for building this project. Then, once you have a solid plan, break it down into small, iterative chunks that build on each other. Look at these chunks and then go another round to break it into small steps. Review the results and make sure that the steps are small enough to be implemented safely with strong testing, but big enough to move the project forward. Iterate until you feel that the steps are right sized for this project.**

**From here you should have the foundation to provide a series of prompts for a code-generation LLM that will implement each step in a test-driven manner. Prioritize best practices, incremental progress, and early testing, ensuring no big jumps in complexity at any stage. Make sure that each prompt builds on the previous prompts, and ends with wiring things together. There should be no hanging or orphaned code that isn't integrated into a previous step.**

**Make sure and separate each prompt section. Use markdown. Each prompt should be tagged as text using code tags. The goal is to output prompts, but context, etc is important as well.** 

\<SPEC\>



- then I take the markdown calude creates and put that in prompt_plan.md in root


## todo.md creation:

I do a git pull back in openhands, and issue the following prompt in it

``Can you make a todo.md that I can use as a checklist? Be thorough. put this todo.md in the root dir``

this can be used to cross out todo steps as they complete


