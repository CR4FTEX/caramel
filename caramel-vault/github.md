# GitHub repository

`CR4FTEX/caramel` on GitHub is private, with the wiki off (`gh repo view`). How people get access and submit work is in `CONTRIBUTING.md`; this note covers how the repository's forms, labels, Actions quota and git setup behave. Each fact names the GitHub docs page or git source it was checked against.

## Issue forms

- `.github/ISSUE_TEMPLATE/config.yml` sets `blank_issues_enabled: false`. That hides the blank issue only from read and triage roles; write access or above still sees "Blank issue", marked "Maintainers only" (GitHub docs, "Configuring issue templates for your repository").
- In a private repository owned by a personal account, collaborators can only have write access (GitHub docs, "Permission levels for a personal account repository"). So everyone who can see this repository keeps the blank issue: the three forms (Idea / feature, Bug report, Art / texture) are the default, not a gate.
- A form's `labels` apply only when the label already exists in the repository (GitHub docs, "Syntax for issue forms"). `bug` is one of GitHub's default labels (GitHub docs, "Managing labels"); `idea` and `art` are not, and exist here because they were created in the repository (`gh label list`). A form whose label is renamed or deleted labels nothing, with no error.
- Issue attachments take only the file types GitHub lists: `.zip` is on the list, and `.ogg` and `.mcmeta` are not (GitHub docs, "Attaching files"). Art sent through an issue goes in one `.zip`.

## Traps

- **A dropdown option named "None" invalidates the form.** GitHub reserves the word to mean "no choice" and reports "options must not include the reserved word, none" (GitHub docs, "Common validation errors when creating issue forms"). The Idea form's vanilla option is "No spin-off (vanilla change)" for that reason.
- **`git pull origin main` stops when both sides have new commits.** Since git 2.33.1, with neither `pull.rebase` nor `pull.ff` configured, git ends with "fatal: Need to specify how to reconcile divergent branches." (`builtin/pull.c` at tag v2.33.1). `git pull --no-rebase --no-edit origin main` merges without opening an editor when the merge has no conflict. After a conflict is fixed, `git commit --no-edit` finishes the merge without an editor.

## Actions quota

GitHub Free gives a personal account 2,000 Actions minutes a month and 500 MB of storage shared by Actions artifacts and GitHub Packages, across all of that account's private repositories and charged to the repository owner. The Actions cache has its own allowance, 10 GB per repository (https://docs.github.com/en/billing/concepts/product-billing/github-actions). [[ci]] covers how the workflow spends little of either.

## Commit identity

The owner's clone sets the owner's GitHub noreply address in its local git config (`git config --local user.email`). Local config stays in that clone's `.git/config` and is not cloned, so it covers commits made in that clone only; a collaborator's commits carry the collaborator's own identity.
