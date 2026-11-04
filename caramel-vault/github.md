# GitHub repository

`CR4FTEX/caramel` on GitHub; `gh repo view CR4FTEX/caramel --json visibility` reads its visibility. How people get access and submit work is in `CONTRIBUTING.md`; this note covers how the repository's forms, labels, protection of `main`, Actions allowance and git setup behave. Each fact names the GitHub docs page or git source it was checked against.

## Issue forms

- `.github/ISSUE_TEMPLATE/config.yml` sets `blank_issues_enabled: false`. That hides the blank issue only from read and triage roles; write access or above still sees "Blank issue", marked "Maintainers only" (GitHub docs, "Configuring issue templates for your repository").
- In a repository owned by a personal account, collaborators can only have write access (GitHub docs, "Permission levels for a personal account repository"), so every collaborator keeps the blank issue. Everyone else has the read access a public repository gives, and gets the forms only (GitHub docs, "Configuring issue templates for your repository").
- A form's `labels` apply only when the label already exists in the repository (GitHub docs, "Syntax for issue forms"). `bug` is one of GitHub's default labels (GitHub docs, "Managing labels"); `idea` and `art` are not, and exist here because they were created in the repository (`gh label list`). A form whose label is renamed or deleted no longer adds it (same page).
- Issue attachments take only the file types GitHub lists: `.zip` is on the list, and `.ogg` and `.mcmeta` are not (GitHub docs, "Attaching files"). Art sent through an issue goes in one `.zip`.

## Traps

- **A dropdown option named "None" invalidates the form.** GitHub reserves the word to mean "no choice" and reports "options must not include the reserved word, none" (GitHub docs, "Common validation errors when creating issue forms"). The Idea form's vanilla option is "No outside source (vanilla change)" for that reason.
- **`git pull origin main` stops when both sides have new commits.** Since git 2.33.1, with neither `pull.rebase` nor `pull.ff` configured, git ends with "fatal: Need to specify how to reconcile divergent branches." (`builtin/pull.c` at tag v2.33.1). `git pull --no-rebase --no-edit upstream main` (`upstream` is the main repository, as `CONTRIBUTING.md` adds it to a fork's clone) merges without opening an editor when the merge has no conflict. After a conflict is fixed, `git commit --no-edit` finishes the merge without an editor (git docs, `git-merge` and `git-commit`, `--no-edit`).

## Protecting `main`

- A ruleset named "Protect main" (`gh api repos/CR4FTEX/caramel/rulesets`) targets the default branch and has four rules: `deletion`, `non_fast_forward`, `pull_request` (0 approvals) and `required_status_checks` with the context `build`, which is the job's name as `gh pr checks` prints it ([[ci]], "Traps", says why no path filter may skip it). The Admin role (actor id 5) bypasses it "always", so the owner can still commit to `main` directly.
- The ruleset holds only while the repository is public: on GitHub Free a private repository has neither rulesets nor branch protection, and reading either with `gh api` returns HTTP 403, "Upgrade to GitHub Pro or make this repository public to enable this feature."
- **Workflows of outside contributors** wait for the owner's approval: `PUT /repos/{owner}/{repo}/actions/permissions/fork-pr-contributor-approval` with `approval_policy=all_external_contributors`. The default, `first_time_contributors`, lets a contributor whose earlier pull request was merged run workflows unapproved.

## Actions allowance

Standard GitHub-hosted runners are free in a public repository. GitHub's page gives artifact storage (500 MB on GitHub Free, shared with GitHub Packages) and the Actions cache (10 GB per repository) as separate allowances, and frames its breakdown around private repositories (https://docs.github.com/en/billing/concepts/product-billing/github-actions).

## Commit identity

The owner's clone sets the owner's GitHub noreply address in its local git config (`git config --local user.email`). `--local` writes to the clone's own `.git/config` (git docs, `git-config`, `--local`), which `git clone` does not copy: in a clone of the owner's clone, `git config --local user.email` prints nothing and exits 1. A collaborator's commits carry the identity set in the collaborator's own git config.
