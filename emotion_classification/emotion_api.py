import numpy as np
import librosa
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from tensorflow.keras.models import load_model
import pickle
import os
from pydantic import BaseModel
import uuid

app = FastAPI(
    title="语音情绪识别API",
    description="基于CNN的语音情绪分类服务",
    version="1.0"
)

# 允许跨域
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 加载模型和标签编码器
model = load_model('cnn_model_split.keras')
with open('processed_data/label_encoder.pkl', 'rb') as f:
    label_encoder = pickle.load(f)

# 加载训练时的参数
max_len = int(np.load('processed_data/max_len.npy'))
n_mfcc = 40
sample_rate = 16000

class EmotionResponse(BaseModel):
    emotion: str
    probabilities: dict
    status: str

def extract_mfcc(file_path: str):
    """提取MFCC特征"""
    try:
        signal, sr = librosa.load(file_path, sr=sample_rate)
        mfcc = librosa.feature.mfcc(y=signal, sr=sr, n_mfcc=n_mfcc)
        mfcc = mfcc.T
        
        # 使用训练时的max_len进行填充
        pad_width = max_len - mfcc.shape[0]
        if pad_width > 0:
            mfcc_padded = np.pad(mfcc, ((0, pad_width), (0, 0)), mode='constant')
        else:
            mfcc_padded = mfcc[:max_len, :]
            
        return np.expand_dims(mfcc_padded, axis=[0, -1])
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"音频处理错误: {str(e)}")

@app.get("/")
async def root():
    """API 根路径，返回基本信息"""
    return {
        "message": "语音情绪识别 API",
        "version": "1.0",
        "endpoints": {
            "POST /predict": "上传音频文件进行情绪分析"
        }
    }

@app.post("/predict", response_model=EmotionResponse)
async def predict_emotion(file: UploadFile = File(...)):
    """预测语音情绪"""
    if not file.filename.lower().endswith('.wav'):
        raise HTTPException(status_code=400, detail="仅支持WAV格式文件")
    
    # 使用唯一ID创建临时文件
    temp_file = f"temp_{uuid.uuid4()}_{file.filename}"
    
    try:
        # 保存临时文件
        with open(temp_file, "wb") as buffer:
            content = await file.read()
            buffer.write(content)
        
        # 提取特征
        features = extract_mfcc(temp_file)
        
        # 预测
        predictions = model.predict(features)
        predicted_index = np.argmax(predictions[0])
        emotion = label_encoder.inverse_transform([predicted_index])[0]
        
        # 获取所有类别的概率
        probabilities = {
            label: float(prob) 
            for label, prob in zip(label_encoder.classes_, predictions[0])
        }
        
        # 删除临时文件
        os.remove(temp_file)
        
        return {
            "emotion": emotion,
            "probabilities": probabilities,
            "status": "success"
        }
    except Exception as e:
        if os.path.exists(temp_file):
            os.remove(temp_file)
        raise HTTPException(status_code=500, detail=f"预测错误: {str(e)}")

# 添加一个简单的健康检查端点
@app.get("/health")
async def health_check():
    """健康检查端点"""
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    print("API 文档可在 http://localhost:5000/docs 访问")
    uvicorn.run(app, host="localhost", port=5000)