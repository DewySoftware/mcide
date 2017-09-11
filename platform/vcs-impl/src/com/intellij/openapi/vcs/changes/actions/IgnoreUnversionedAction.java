/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.openapi.vcs.changes.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ui.IgnoreUnversionedDialog;
import com.intellij.openapi.vcs.changes.ui.OldChangesBrowserBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.openapi.vcs.changes.ui.ChangesListView.UNVERSIONED_FILES_DATA_KEY;
import static com.intellij.util.containers.UtilKt.isEmpty;

public class IgnoreUnversionedAction extends AnAction {

  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getRequiredData(CommonDataKeys.PROJECT);

    if (!ChangeListManager.getInstance(project).isFreezedWithNotification(null)) {
      List<VirtualFile> files = e.getRequiredData(UNVERSIONED_FILES_DATA_KEY).collect(Collectors.toList());
      OldChangesBrowserBase<?> browser = e.getData(OldChangesBrowserBase.DATA_KEY);
      Runnable callback = browser == null ? null : () -> {
        browser.rebuildList();
        //noinspection unchecked
        browser.getViewer().excludeChanges((List)files);
      };

      IgnoreUnversionedDialog.ignoreSelectedFiles(project, files, callback);
    }
  }

  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabled(e.getProject() != null && !isEmpty(e.getData(UNVERSIONED_FILES_DATA_KEY)));
  }
}