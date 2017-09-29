package org.mobicents.protocols.ss7.tcap.data.local;

import org.mobicents.protocols.ss7.sccp.SccpProvider;
import org.mobicents.protocols.ss7.sccp.message.SccpDataMessage;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.tcap.TCAPStackImpl;
import org.mobicents.protocols.ss7.tcap.api.TCAPException;
import org.mobicents.protocols.ss7.tcap.api.TCAPStack;
import org.mobicents.protocols.ss7.tcap.data.DialogImpl;
import org.mobicents.protocols.ss7.tcap.data.IDialog;
import org.mobicents.protocols.ss7.tcap.data.IDialogDataStorage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by piotr.sokolowski on 2017-06-08.
 */
public class LocalDialogDataStorage implements IDialogDataStorage {
    private final LocalTimerFacility timerFacility;
    private ConcurrentHashMap<Long,IDialog> dialogs=new ConcurrentHashMap<>();
    private long curDialogId = 0;

    private TCAPStackImpl stack;

    public void init(TCAPStackImpl stack) {
        this.stack=stack;
    }

    public LocalDialogDataStorage(LocalTimerFacility timerFacility) {
        this.timerFacility=timerFacility;
    }

    private boolean checkAvailableTxId(Long id) {
        if (!this.dialogs.containsKey(id))
            return true;
        else
            return false;
    }

    private synchronized Long getAvailableTxId(TCAPStack stack) throws TCAPException {
        if (this.dialogs.size() >= stack.getMaxDialogs())
            throw new TCAPException("Current dialog count exceeds its maximum value");

        while (true) {
//            Long id;
//            if (!currentDialogId.compareAndSet(this.stack.getDialogIdRangeEnd(), this.stack.getDialogIdRangeStart() + 1)) {
//                id = currentDialogId.getAndIncrement();
//            } else {
//                id = this.stack.getDialogIdRangeStart();
//            }
//            if (checkAvailableTxId(id))
//                return id;



            if (this.curDialogId < stack.getDialogIdRangeStart())
                this.curDialogId = stack.getDialogIdRangeStart() - 1;
            if (++this.curDialogId > stack.getDialogIdRangeEnd())
                this.curDialogId = stack.getDialogIdRangeStart();
            Long id = this.curDialogId;
            if (checkAvailableTxId(id))
                return id;
        }
    }

    protected void resetDialogIdValueAfterRangeChange(TCAPStack stack) {
        if (this.curDialogId < stack.getDialogIdRangeStart())
            this.curDialogId = stack.getDialogIdRangeStart();
        if (this.curDialogId >= stack.getDialogIdRangeEnd())
            this.curDialogId = stack.getDialogIdRangeEnd() - 1;

        // if (this.currentDialogId.longValue() < this.stack.getDialogIdRangeStart())
        // this.currentDialogId.set(this.stack.getDialogIdRangeStart());
        // if (this.currentDialogId.longValue() >= this.stack.getDialogIdRangeEnd())
        // this.currentDialogId.set(this.stack.getDialogIdRangeEnd() - 1);
    }

    public void beginTransaction() {
    }

    public void commitTransaction() {

    }

    @Override
    public void sendToSccp(SccpProvider sccpProvider, SccpDataMessage msg) throws IOException {
        sccpProvider.send(msg);
    }

    @Override
    public IDialog createDialog(SccpAddress localAddress, SccpAddress remoteAddress, Long id,
                                boolean structured, int seqControl) throws TCAPException{
        if (id == null) {
            id = this.getAvailableTxId(stack);
        } else {
            if (!checkAvailableTxId(id)) {
                throw new TCAPException("Suggested local TransactionId is already present in system: " + id);
            }
        }
        if(stack.getMaxDialogs()>0 && dialogs.size()>=stack.getMaxDialogs())
            throw new TCAPException("Maximum number of dialogs exceeded");
        LocalDialogData dd=new LocalDialogData(this);
        dd.setLocalAddress(localAddress);
        dd.setRemoteAddress(remoteAddress);
        if (id != null) {
            dd.setLocalTransactionIdObject(id);
            dd.setLocalTransactionId(id);
        }
        dd.setStructured(structured);
        dd.setSeqControl(seqControl);
        DialogImpl dlg=new DialogImpl(stack.getProviderImpl(),dd);
        dd.setDialog(dlg);
        dd.setIdleTaskTimeout(stack.getDialogIdleTimeout());
        dd.setStartDialogTime(System.currentTimeMillis());

        // start
        dlg.startIdleTimer();
        if(structured)
            dialogs.put(dlg.getLocalDialogId(),dlg);
        return dlg;
    }

    @Override
    public IDialog getDialog(Long dialogId) {
        return dialogs.get(dialogId);
    }

    @Override
    public void removeDialog(IDialog d) {
        dialogs.remove(d.getLocalDialogId(),d);

    }

    @Override
    public int getSize() {
        return dialogs.size();
    }

    @Override
    public void clear() {
        dialogs.clear();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public ITimerFacility getTimerFacility() {
        return timerFacility;
    }
}
