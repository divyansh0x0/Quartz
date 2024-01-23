package app.dialogs;

import material.utils.ComponentMover;

public class DialogFactory {
    private static AphroditeDialog dialogInstance;
    private static ComponentMover _componentMover = new ComponentMover();

    public static void showErrorDialog(String error) {
//        disposeLastDialog();
//        registerDialogMover();
        new ErrorDialog(error);
    }

    //--------------------------------------------------------------------------------
    //                                PRIVATE
    //--------------------------------------------------------------------------------
    private static void disposeLastDialog() {
        if (dialogInstance != null) {
            _componentMover.deregisterComponent(dialogInstance);
            dialogInstance.close();
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
