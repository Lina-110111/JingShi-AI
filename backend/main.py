from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from pathlib import Path
import uuid

# 创建 FastAPI 应用
app = FastAPI(
    title="净识AI后端服务",
    description="AI垃圾分类识别系统后端接口",
    version="1.0.0"
)

# 跨域配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 图片上传目录
UPLOAD_DIR = Path("uploads")
UPLOAD_DIR.mkdir(exist_ok=True)


@app.get("/")
async def root():
    return {
        "message": "欢迎使用净识AI后端服务！",
        "status": "running",
        "port": 8084
    }


@app.get("/health")
async def health():
    return {
        "status": "success",
        "message": "后端运行正常",
        "service": "JingShi-AI"
    }


@app.post("/api/v1/recognition/upload")
async def upload_image(file: UploadFile = File(...)):

    # 检查图片格式
    allowed_types = [
        "image/jpeg",
        "image/png",
        "image/webp"
    ]

    if file.content_type not in allowed_types:
        return {
            "success": False,
            "message": "暂不支持该图片格式"
        }

    # 生成唯一文件名
    suffix = Path(file.filename or "").suffix.lower()

    if suffix not in [".jpg", ".jpeg", ".png", ".webp"]:
        suffix = ".jpg" if file.content_type == "image/jpeg" else (
            ".png" if file.content_type == "image/png" else ".webp"
        )

    filename = f"{uuid.uuid4().hex}{suffix}"
    file_path = UPLOAD_DIR / filename

    # 保存图片
    content = await file.read()

    if not content:
        return {
            "success": False,
            "message": "上传的图片为空"
        }

    if len(content) > 10 * 1024 * 1024:
        return {
            "success": False,
            "message": "图片不能超过10MB"
        }

    with open(file_path, "wb") as f:
        f.write(content)

    return {
        "success": True,
        "message": "图片上传成功，等待接入AI识别模型",
        "filename": filename,
        "model_status": "not_connected"
    }
