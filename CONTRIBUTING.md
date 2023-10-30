## Contributing to Minimalist-Java

First off, thank you for considering contributing to Minimalist-Java. It's people like you that make Minimalist-Java
such a great tool.

### Where do I go from here?

If you've noticed a bug or have a feature request, make one! It's generally best if you get confirmation of your bug or
approval for your feature request this way before starting to code.

### Versioning:

We follow the [Semantic Versioning 2.0.0](https://semver.org/) standard. Here's a quick rundown:

- **Major versions**: Introduce significant changes to the library that make it backward incompatible. The major version
  must be incremented if any backward incompatible changes are introduced.

- **Minor versions**: Add functionality in a manner that is backward compatible. For example, adding a new feature is a
  minor change as long as it doesn't break existing functionality.

- **Patch versions**: Introduce backward-compatible bug fixes. If a bug is found and fixed, that's a patch. These
  changes generally do not affect the software's functionality from an end-user's point-of-view.

- **Beta**: Represents a version that is feature complete but might have known limitations or bugs. Beta software is
  useful for external testing with a larger group to gain feedback for the final release.

When contributing, consider the impact of your changes and appropriately version your contributions.

### Fork & create a branch

If this is something you think you can fix, then fork Minimalist-Java and create a branch with a descriptive name.

A good branch name would be (where issue #325 is the ticket you're working on):

```bash
git checkout -b feature/325-add-jetpack
```

### Guidelines for Commit Messages:

Always refer to the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) standard when structuring
your commit messages.

Using the provided list, your commits should ideally look like:

- `🚧 build:` Changes related to the build system or external dependencies.
- `🤖 ci:` Changes to our CI configuration files and scripts.
- `🧹 chore:` Regular maintenance and minor tasks.
- `📚 docs:` Documentation only changes.
- `🎉 feat:` A new feature.
- `🐛 fix:` A bug fix.
- `🐎 perf:` Performance improvements.
- `🔨 refactor:` Code changes that neither fix a bug nor add a feature.
- `🔀 revert:` Reverting previous commits.
- `💄 style:` Changes that do not affect the meaning of the code (white-space, formatting, etc).
- `✅ test:` Adding missing tests or correcting existing tests.
- `🏗️ wip:` Work in progress commits (use sparingly).

Examples:

- `🚧 build: update Gradle version`
- `🤖 ci: add a new job to GitHub Actions`
- `♻️ chore: update package.json dependencies`
- `📚 docs: update README with new section`
- `✨ feat: introduce new analytics feature`
- `🐛 fix: resolve authentication issue`
- `🐎 perf: optimize database queries`
- `🔨 refactor: organize utils into separate modules`
- `🔀 revert: revert commit abc123`
- `💄 style: apply code formatter`
- `✅ test: increase coverage for utils`
- `🏗️ wip: implementing the new module`

Additional notes:

- Use the present tense ("Add feature" not "Added feature")
- Limit the first line to 72 characters or less

By adhering to the Conventional Commits standard combined with the provided emoji guide, it will be much easier to
review changes, manage versions, and generate changelogs.

### Release Process:

Our release process is automated using GitHub Actions:

1. The version is extracted from the `pom.xml` file.
2. The code is built and tested.
3. If tests pass, the release version is set (i.e., `-SNAPSHOT` is removed).
4. Code is tagged and pushed with the release version.
5. Release notes are extracted from `CHANGELOG.md`.
6. A GitHub release is created using the extracted release notes.
7. The version in `pom.xml` is bumped to the next `-SNAPSHOT` version for the next development cycle.

### Getting your changes merged

Once you feel good about your changes, it's time to get your changes merged!

1. Push your changes to your branch on your forked repository.
2. Submit a pull request to the main Minimalist-Java repo.
