package app.dialogs;

import material.utils.ComponentMover;

public class DialogFactory {
    private static QuartzDialog dialogInstance;
    private static ComponentMover _componentMover = new ComponentMover();

    public static void showErrorDialog(String error) {
        new StringDialog(error,DialogType.ERROR);
    }
    public static void showSuccessDialog(String success) {
        new StringDialog(success,DialogType.SUCCESS);
    }
    public static void showWarningDialog(String warning) {
        new StringDialog(warning,DialogType.WARN);
    }

    //--------------------------------------------------------------------------------
    //                                PRIVATE
    //--------------------------------------------------------------------------------
    private static void disposeLastDialog() {
        if (dialogInstance != null) {
            _componentMover.deregisterComponent(dialogInstance);
            dialogInstance.dispose();
            dialogInstance = null;
        }
    }

    private static void registerDialogMover() {
        if(dialogInstance != null){
            _componentMover.registerComponent(dialogInstance);
        }
    }
}
