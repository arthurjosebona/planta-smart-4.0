import { useEffect, useRef, useState } from 'react';
import { Canvas } from '@react-three/fiber';
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

export function OrderViewer({ state, ...dimProps }: OrderViewerProps) {
  // Gira sozinho quando ocioso; pausa durante a interação e retoma após o delay.
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
