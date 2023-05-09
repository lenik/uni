package net.bodz.uni.autocommit.classifiers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.uni.autocommit.classifier.AbstractItemClassifier;
import net.bodz.uni.autocommit.classifier.ChangeGroup;
import net.bodz.uni.autocommit.classifier.Changeset;
import net.bodz.uni.autocommit.classifier.EditElement;
import net.bodz.uni.autocommit.classifier.EditElements;
import net.bodz.uni.autocommit.classifier.StatusEntry;

public class BeatLogs
        extends AbstractItemClassifier {

    static Pattern beatLogPath = compile("/(\\d+-\\d+-\\d+)/beatLog$");

    static ChangeGroup beatLogGroup;
    static {
        beatLogGroup = new ChangeGroup();
    }

    @Override
    public boolean classify(Changeset set, StatusEntry entry) {
        String path = entry.getPath();
        Matcher matcher = beatLogPath.matcher(path);
        if (matcher.find()) {
            String date = matcher.group(1);
            EditElements edits = set.lazyCreate(beatLogGroup);
            EditElement edit = EditElement.fromStatusEntry(entry);
            edits.add(edit);
            return true;
        }
        return false;
    }

}
