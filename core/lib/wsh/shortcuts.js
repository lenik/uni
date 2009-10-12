
function createShortcut(src, dst, workdir, icon, desc) {
    var shortcut = SHELL.CreateShortcut(dst);
    shortcut.TargetPath = src;
    if (workdir != null)
        shortcut.WorkingDirectory = workdir;
    if (icon != null)
        shortcut.IconLocation = icon;
    if (desc != null)
        shortcut.Description = desc;
    shortcut.save();
}
