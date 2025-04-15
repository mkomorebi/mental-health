import os
import numpy as np
import librosa
import pickle
from sklearn.preprocessing import LabelEncoder
from tensorflow.keras.utils import to_categorical

# ==========================
# 参数配置
# ==========================
data_path = 'emotion_classification/data'  # CASIA 数据路径
emotions = ['angry', 'fear', 'happy', 'neutral', 'sad', 'surprise']
n_mfcc = 40                      # MFCC维度
output_dir = 'processed_data'   # 输出保存路径
sample_rate = 16000             # 默认采样率

# ==========================
# 数据准备
# ==========================
X = []  # 存放MFCC特征
y = []  # 存放标签

print("🔍 开始读取音频并提取 MFCC 特征...")

for emotion in emotions:
    emotion_path = os.path.join(data_path, emotion)
    if not os.path.exists(emotion_path):
        print(f"⚠️ 目录不存在: {emotion_path}")
        continue
    for file_name in os.listdir(emotion_path):
        if not file_name.lower().endswith('.wav'):
            continue
        file_path = os.path.join(emotion_path, file_name)

        try:
            # 加载音频
            signal, sr = librosa.load(file_path, sr=sample_rate)

            # 提取 MFCC 特征
            mfcc = librosa.feature.mfcc(y=signal, sr=sr, n_mfcc=n_mfcc)
            mfcc = mfcc.T  # 转置成 (时间帧, 维度)

            X.append(mfcc)
            y.append(emotion)

        except Exception as e:
            print(f"❌ 无法处理文件 {file_name}：{e}")

# ==========================
# 序列长度统一（零填充）
# ==========================
print("📏 正在对 MFCC 特征进行零填充处理...")

max_len = max(mfcc.shape[0] for mfcc in X)
X_pad = []

for mfcc in X:
    pad_width = max_len - mfcc.shape[0]
    if pad_width > 0:
        mfcc_padded = np.pad(mfcc, ((0, pad_width), (0, 0)), mode='constant')
    else:
        mfcc_padded = mfcc[:max_len, :]
    X_pad.append(mfcc_padded)

X_pad = np.array(X_pad)  # 最终形状：(样本数, 时间帧, 特征维度)

# ==========================
# 标签编码（One-Hot）
# ==========================
print("🏷️ 正在编码标签...")

label_encoder = LabelEncoder()
y_encoded = label_encoder.fit_transform(y)
y_categorical = to_categorical(y_encoded)

# ==========================
# 保存数据和标签
# ==========================
os.makedirs(output_dir, exist_ok=True)
np.save(os.path.join(output_dir, 'X_pad.npy'), X_pad)
np.save(os.path.join(output_dir, 'y_categorical.npy'), y_categorical)

# 保存标签编码器和max_len
with open(os.path.join(output_dir, 'label_encoder.pkl'), 'wb') as f:
    pickle.dump(label_encoder, f)
np.save(os.path.join(output_dir, 'max_len.npy'), np.array(max_len))

print("✅ 数据预处理完成！已保存至：", output_dir)
print(f"📦 特征数据: {X_pad.shape}，标签数据: {y_categorical.shape}")
print(f"🔖 最大序列长度: {max_len}")
print("📚 标签类别编码: ", dict(zip(label_encoder.classes_, label_encoder.transform(label_encoder.classes_))))