# vlc-rtsp-player

Plugin para reproducir RTSP Usando VLC

## Install

```bash
npm install vlc-rtsp-player
npx cap sync
```

## API

<docgen-index>

* [`play(...)`](#play)
* [`pause()`](#pause)
* [`updateStream(...)`](#updatestream)
* [`checkConnection(...)`](#checkconnection)
* [`checkOverlayPermission()`](#checkoverlaypermission)
* [`requestOverlayPermission()`](#requestoverlaypermission)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### play(...)

```typescript
play(options: { url: string; }) => Promise<void>
```

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

--------------------


### pause()

```typescript
pause() => Promise<void>
```

--------------------


### updateStream(...)

```typescript
updateStream(option: { url: string; }) => Promise<void>
```

| Param        | Type                          |
| ------------ | ----------------------------- |
| **`option`** | <code>{ url: string; }</code> |

--------------------


### checkConnection(...)

```typescript
checkConnection(option: { url: string; }) => Promise<void>
```

| Param        | Type                          |
| ------------ | ----------------------------- |
| **`option`** | <code>{ url: string; }</code> |

--------------------


### checkOverlayPermission()

```typescript
checkOverlayPermission() => Promise<void>
```

--------------------


### requestOverlayPermission()

```typescript
requestOverlayPermission() => Promise<void>
```

--------------------

</docgen-api>
