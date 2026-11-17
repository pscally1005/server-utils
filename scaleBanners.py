from pathlib import Path
from PIL import Image

# Folder locations
source_folder = Path("Banners")
output_folder = Path("BannersScaled")

# Create output folder if it doesn't exist
output_folder.mkdir(exist_ok=True)

# Supported image formats
extensions = {".png", ".jpg", ".jpeg", ".webp"}

for image_path in source_folder.iterdir():
    if image_path.suffix.lower() not in extensions:
        continue

    with Image.open(image_path) as img:
        if img.size == (120, 240):
            img = img.resize((24, 48), Image.Resampling.LANCZOS)
            print(f"Resized: {image_path.name}")
        else:
            print(f"Skipped (not 120x240): {image_path.name} - {img.size}")
            continue

        output_path = output_folder / image_path.name
        img.save(output_path)

print("\nDone!")
