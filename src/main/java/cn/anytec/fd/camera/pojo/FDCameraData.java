package cn.anytec.fd.camera.pojo;

public class FDCameraData {
    public static final int MAX_FACE_ITEM_PER_FRAME = 10;
    public int mFaceNum = 0;
    public String mStrMac = "";
    public boolean mHasNewFd = false;
    public FaceDefine[] mFaceItem = new FaceDefine[MAX_FACE_ITEM_PER_FRAME];
    //public List<FaceDefine> mFaceItem = new LinkedList<>();
    public long timestamp = System.currentTimeMillis();
    public int mJpgSize = 0;
    public byte[] mJpgData = null;

    @Override
    public String toString() {
        return "FDCameraData [mStrMac=" + mStrMac + ", mFaceNum=" + mFaceNum + ", mHasNewFd=" + mHasNewFd
                + ", mJpgSize=" + mJpgSize + "]";
    }


}
