# How to Contribute
Simple Scala Bootstrap an open source project which strongly encourages, and rely on, community contributions

## Code of Conduct
We have adopted the Contributor Covenant as its [Code of Conduct](https://www.contributor-covenant.org/), and we expect project participants to adhere to it. Please read the full text so that you can understand what actions will and will not be tolerated.

## Open Development
All work happens directly on GitHub. Both core team members and external contributors send pull requests which go through the same review process.

## Semantic Versioning
Simple Scala Bootstrap follows semantic versioning. We release patch versions for critical bugfixes, minor versions for 
new features or non-essential changes, and major versions for any breaking changes. When we make breaking changes, we 
also introduce deprecation warnings in a minor version so that our users learn about the upcoming changes and migrate 
their code in advance. Learn more about our commitment to stability and incremental migration in our versioning policy.

## Branch Organization
Submit all changes directly to the main branch. We don’t use separate branches for development or for upcoming releases. 
We do our best to keep main in good shape, with all tests passing.

Code that lands in main must be compatible with the latest stable release. It may contain additional features, but no 
breaking changes. We should be able to release a new minor version from the tip of main at any time.

## Feature Flags
To keep the main branch in a releasable state, breaking changes and experimental features must be gated behind a feature flag.

## Bugs
### Where to Find Known Issues
We are using GitHub Issues for our public bugs. We keep a close eye on this and try to make it clear when we have an internal fix in progress. Before filing a new task, try to make sure your problem doesn’t already exist.

### Reporting New Issues
The best way to get your bug fixed is to provide a reduced test case. This JSFiddle template is a great starting point.

## Proposing a Change
If you intend to change the public API, or make any non-trivial changes to the implementation, we recommend filing an issue. 
This lets us reach an agreement on your proposal before you put significant effort into it.

## Sending a Pull Request
The core team is monitoring for pull requests. We will review your pull request 
and either merge it, request changes to it, or close it with an explanation.