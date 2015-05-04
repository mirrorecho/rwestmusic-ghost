# change directory to tokei directory (directory containint the sync script)
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

read -p "Enter commit message for ghost changes:" commit_msg
git add --all .
git commit -m "$commit_msg"
git pull --rebase
git push