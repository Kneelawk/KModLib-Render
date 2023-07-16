Changes:

* Added `kmodlib-blockmodel:model_ref` model layer for referencing other models in a layered model.
    * This allows the combination of different models with different render materials.
* Added a face normal direction parameter to `BakedSpriteSupplier.getBlockSprite`.
    * This allows sprite suppliers to differentiate based on the side of the block that the sprite is being retrieved
      for.
