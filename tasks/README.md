# devsupwiz-tasks
Provides some useful tasks for the developer setup wizard.

## set-hostname
Sets a new hostname for the virtual machine (VM).
```xml
<set-hostname task-class="org.fuin.devsupwiz.tasks.hostname.SetHostnameTask" />
```

<a href="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/set-hostname.png" target="_blank"><img src="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/set-hostname.png" width="320" height="335" alt="Set hostname screen"></a>

# create-git-config
Creates and populates the "~/.gitconfig" file.
```xml
<create-git-config task-class="org.fuin.devsupwiz.tasks.gitsetup.CreateGitConfigTask" />
```

<a href="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/create-git-config.png" target="_blank"><img src="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/create-git-config.png" width="320" height="335" alt="Create git config screen"></a>

# setup-git-ssh
Generates a new key pair and adds it to the "~/.ssh/config" file. The public key is also submitted to the git provider (Bitbucket, Github) using a REST API.
```xml
<setup-git-ssh id="1" provider="bitbucket" host="bitbucket.org" 
           task-class="org.fuin.devsupwiz.tasks.gitsetup.SetupGitSshTask" />
```

<a href="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/setup-git-ssh.png" target="_blank"><img src="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/setup-git-ssh.png" width="320" height="335" alt="Setup git ssh"></a>

# git-clone
Clones one or more git repositories. Requires that a valid SSH key is installed (See "setup-git-ssh" task).
```xml
<git-clone id="1" target-dir="~/git" task-class="org.fuin.devsupwiz.tasks.gitsetup.GitCloneTask" >
  <repository>git@bitbucket.org:my_account/my-project.git</repository>
  <repository>git@bitbucket.org:my_account/another-one.git</repository>
  <repository>git@bitbucket.org:my_account/whatever.git</repository>
</git-clone>
```

<a href="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/clone-git-repositories.png" target="_blank"><img src="https://github.com/fuinorg/dev-setup-wizard/raw/master/tasks/doc/clone-git-repositories.png" width="320" height="335" alt="Setup git ssh"></a>

