/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\coinplug-eng\\AndroidStudioProjects\\meta_android_app\\app\\src\\main\\aidl\\com\\metadium\\IKeepinService.aidl
 */
package com.metadium;
public interface IKeepinService extends android.os.IInterface
{
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements com.metadium.IKeepinService
    {
        private static final String DESCRIPTOR = "com.metadium.IKeepinService";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an com.metadium.IKeepinService interface,
         * generating a proxy if needed.
         */
        public static com.metadium.IKeepinService asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof com.metadium.IKeepinService))) {
                return ((com.metadium.IKeepinService)iin);
            }
            return new com.metadium.IKeepinService.Stub.Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_requestAddKey:
                {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    String _arg2;
                    _arg2 = data.readString();
                    android.os.ResultReceiver _arg3;
                    if ((0!=data.readInt())) {
                        _arg3 = android.os.ResultReceiver.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg3 = null;
                    }
                    this.requestAddKey(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_requestRemoveKey:
                {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    android.os.ResultReceiver _arg2;
                    if ((0!=data.readInt())) {
                        _arg2 = android.os.ResultReceiver.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg2 = null;
                    }
                    this.requestRemoveKey(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_requestSign:
                {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    android.os.ResultReceiver _arg2;
                    if ((0!=data.readInt())) {
                        _arg2 = android.os.ResultReceiver.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg2 = null;
                    }
                    this.requestSign(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_hasKey:
                {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.hasKey(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result)?(1):(0)));
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }
        private static class Proxy implements com.metadium.IKeepinService
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            @Override public void requestAddKey(String serviceId, String signature, String nonce, android.os.ResultReceiver resultReceiver) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(serviceId);
                    _data.writeString(signature);
                    _data.writeString(nonce);
                    if ((resultReceiver!=null)) {
                        _data.writeInt(1);
                        resultReceiver.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_requestAddKey, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void requestRemoveKey(String serviceId, String metaId, android.os.ResultReceiver resultReceiver) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(serviceId);
                    _data.writeString(metaId);
                    if ((resultReceiver!=null)) {
                        _data.writeInt(1);
                        resultReceiver.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_requestRemoveKey, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void requestSign(String serviceId, String nonce, android.os.ResultReceiver resultReceiver) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(serviceId);
                    _data.writeString(nonce);
                    if ((resultReceiver!=null)) {
                        _data.writeInt(1);
                        resultReceiver.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_requestSign, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public boolean hasKey(String serviceId) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(serviceId);
                    mRemote.transact(Stub.TRANSACTION_hasKey, _data, _reply, 0);
                    _reply.readException();
                    _result = (0!=_reply.readInt());
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }
        static final int TRANSACTION_requestAddKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_requestRemoveKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_requestSign = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_hasKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    }
    public void requestAddKey(String serviceId, String signature, String nonce, android.os.ResultReceiver resultReceiver) throws android.os.RemoteException;
    public void requestRemoveKey(String serviceId, String metaId, android.os.ResultReceiver resultReceiver) throws android.os.RemoteException;
    public void requestSign(String serviceId, String nonce, android.os.ResultReceiver resultReceiver) throws android.os.RemoteException;
    public boolean hasKey(String serviceId) throws android.os.RemoteException;
}
