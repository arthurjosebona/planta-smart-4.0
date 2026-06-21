import { useEffect, useRef, useState } from 'react';
import { Canvas, useThree } from '@react-three/fiber';
import { OrbitControls } from '@react-three/drei';
import { StoreModel } from '@pages/Store/StoreModel';
import { AUTO_ROTATE } from '@config/blockModel';
import { BlockScene } from '@components/organisms/BlockScene/BlockScene';
import styles from '@components/organisms/OrderViewer/orderViewer.module.css';

interface OrderViewerProps {
  state: StoreModel;
  blockW?: number;
  blockD?: number;
  blockH?: number;
  baseT?: number;
  colW?: number;
  colRadius?: number;
  bladeT?: number;
  bladeRecess?: number;
  lidH?: number;
}

// Ajusta o zoom da câmera proporcionalmente à altura do canvas.
// Referência: canvas com 600 px de altura → zoom 1.
// Canvas menor → zoom maior (objeto mantém fração visual consistente).
function ResponsiveCamera() {
  const { camera, size } = useThree();

  useEffect(() => {
    const BASE_HEIGHT = 600;
    const zoom = Math.max(0.4, Math.min(3, BASE_HEIGHT / Math.max(size.height, 80)));
    camera.zoom = zoom;
    // @ts-expect-error — updateProjectionMatrix existe em PerspectiveCamera e OrthographicCamera
    camera.updateProjectionMatrix?.();
  }, [size.height, camera]);

  return null;
}

export function OrderViewer({ state, ...dimProps }: OrderViewerProps) {
  const [autoRotate, setAutoRotate] = useState(true);
  const resumeTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const handleStart = () => {
    if (resumeTimer.current) clearTimeout(resumeTimer.current);
    setAutoRotate(false);
  };

  const handleEnd = () => {
    if (resumeTimer.current) clearTimeout(resumeTimer.current);
    resumeTimer.current = setTimeout(() => setAutoRotate(true), AUTO_ROTATE.resumeDelayMs);
  };

  useEffect(() => () => {
    if (resumeTimer.current) clearTimeout(resumeTimer.current);
  }, []);

  return (
    <Canvas camera={{ position: [0, 1.0, 8], fov: 38 }} className={styles.canvas}>
      <ResponsiveCamera />
      <ambientLight intensity={0.75} />
      <directionalLight position={[4, 6, 5]} intensity={0.7} />
      <directionalLight position={[-4, 2, -4]} intensity={0.3} />
      <BlockScene state={state} {...dimProps} />
      <OrbitControls
        enablePan={false}
        minDistance={3}
        maxDistance={16}
        autoRotate={autoRotate}
        autoRotateSpeed={AUTO_ROTATE.speed}
        onStart={handleStart}
        onEnd={handleEnd}
      />
    </Canvas>
  );
}
