package com.lapiota.pooh.popup.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.bas.types.HashTextMap;
import net.bodz.bas.types.TextMap;

import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;

import com.lapiota.pooh.Activator;
import com.lapiota.pooh.util.BashShells;
import com.lapiota.pooh.util.Classpaths;
import com.lapiota.pooh.util.SysShells;

public class BashWithContextAction extends SimpleAction {

    private ILog log;

    void logError(String mesg) {
        log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, mesg));
    }

    void logWarning(String mesg) {
        log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, mesg));
    }

    public BashWithContextAction() {
        log = Activator.getDefault().getLog();
    }

    IPath expand(IJavaModel javaModel, IPath modelPath) {
        IPath absolute = modelPath;
        if (modelPath.getDevice() == null && modelPath.segmentCount() >= 2) {
            String prefix = modelPath.segment(0);
            IPath tail = modelPath.removeFirstSegments(1);
            IJavaProject projref = javaModel.getJavaProject(prefix);
            if (projref != null) {
                IPath refProjectLocation = ((IResource) projref
                        .getAdapter(IResource.class)).getLocation();
                if (refProjectLocation != null)
                    absolute = refProjectLocation.append(tail);
            }
        }
        return absolute;
    }

    static TextMap<Classpaths> cpCache = new HashTextMap<Classpaths>();

    IPath collectClasspaths(Classpaths container, IJavaProject project) {
        IPath base = null;
        IResource projectRes = (IResource) project.getAdapter(IResource.class);
        if (projectRes != null)
            base = projectRes.getLocation();

        String projectName = project.getElementName();
        Classpaths projectPaths = cpCache.get(projectName);
        if (projectPaths == null) {
            projectPaths = new Classpaths();
            IPathVariableManager pathVars = project.getJavaModel()
                    .getWorkspace().getPathVariableManager();
            try {
                IClasspathEntry[] cpentries = project
                        .getResolvedClasspath(true);
                for (IClasspathEntry cpentry : cpentries) {
                    int kind = cpentry.getEntryKind();
                    IPath path = cpentry.getPath();
                    switch (kind) {
                    case IClasspathEntry.CPE_LIBRARY:
                        // XXX - better way?
                        // may external jar, or internal path to the root.
                        projectPaths.addAbsolute(expand(project.getJavaModel(),
                                path));
                        break;
                    case IClasspathEntry.CPE_PROJECT:
                        String cpName = path.lastSegment();
                        IJavaProject refProject = project.getJavaModel()
                                .getJavaProject(cpName);
                        // recursive add the classpaths
                        collectClasspaths(projectPaths, refProject);
                        break;
                    case IClasspathEntry.CPE_SOURCE:
                        IPath output = cpentry.getOutputLocation();
                        if (output == null)
                            output = project.getOutputLocation();
                        // output: /PROJECT/...
                        output = output.removeFirstSegments(1);

                        // File srcloc =
                        projectPaths.addRelative(base, output);
                        IPath srcExpanded = expand(project.getJavaModel(), path);
                        projectPaths.addSourceFolder(srcExpanded.toFile());
                        break;
                    case IClasspathEntry.CPE_VARIABLE:
                        String varName = path.segment(0);
                        IPath prefix = pathVars.getValue(varName);
                        if (prefix != null) {
                            IPath varExt = path.removeFirstSegments(1);
                            path = prefix.append(varExt);
                            projectPaths.addAbsolute(path);
                        } else {
                            logError("Variable " + varName + " isn't defined.");
                        }
                        break;
                    case IClasspathEntry.CPE_CONTAINER:
                    default:
                        logWarning("Classpath Kind " + kind
                                + " isn't supported. ");
                        // unsupported, ignore.
                        break;
                    }
                }
            } catch (JavaModelException e) {
                logError("Failed to getResolvedClasspath" + e.getMessage());
            }
            cpCache.put(projectName, projectPaths);
        }
        container.merge(projectPaths);
        return base;
    }

    /**
     * <pre>
     * Hello   Project
     *   src     PackageFragmentRoot
     *     hello   PackageFragment
     *       Hello.java CompilationUnit
     *         Hello     SourceType        BinaryType
     *           main      SourceMethod    BinaryMethod
     * </pre>
     */
    @Override
    protected void run(IAction action, Object selectedObject) throws Exception {
        if (!(selectedObject instanceof IJavaElement))
            return;
        IJavaElement javaElement = (IJavaElement) selectedObject;
        IJavaProject javaProject = javaElement.getJavaProject();
        Classpaths paths = new Classpaths();
        // IPath projectLoc =
        collectClasspaths(paths, javaProject);

        IResource res = (IResource) javaElement.getAdapter(IResource.class);
        IPath path;
        if (res == null) {
            throw new NotImplementedException(
                    "Can't adapt java element to resource, need to get the path by project.loc + elm.path");
        }
        path = res.getLocation();
        File dir = path.toFile();
        while (dir != null && !dir.isDirectory())
            dir = dir.getParentFile();
        if (dir != null) {
            Map<String, String> env = new HashMap<String, String>();
            env.putAll(System.getenv());
            // String cp0 = env.get("CLASSPATH");

            // cygwin: maybe using win32 java instead.
            String cp = paths.buildCPString(SysShells.pathSeparator);

            // XXX - replace existing classpath when case insensitive
            env.put("CLASSPATH", cp);
            String title = "for project " + javaProject.getElementName();
            BashShells.open(shell, dir, env, paths.getMainClasses(), title);
        }
    }

}
